package com.fasten.executor_driver.presentation.waitingforclient;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.WaitingForClientUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class WaitingForClientViewModelImpl extends ViewModel implements WaitingForClientViewModel {

  @NonNull
  private final WaitingForClientUseCase waitingForClientUseCase;
  @NonNull
  private final MutableLiveData<ViewState<WaitingForClientViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable actionsDisposable = EmptyDisposable.INSTANCE;

  @Inject
  public WaitingForClientViewModelImpl(@NonNull WaitingForClientUseCase waitingForClientUseCase) {
    this.waitingForClientUseCase = waitingForClientUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new WaitingForClientViewStateIdle());
  }

  @NonNull
  @Override
  public LiveData<ViewState<WaitingForClientViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void startLoading() {
    if (!actionsDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new WaitingForClientViewStatePending());
    actionsDisposable = waitingForClientUseCase.startTheOrder()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new WaitingForClientViewStateIdle());
              navigateLiveData.postValue(WaitingForClientNavigate.NO_CONNECTION);
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    actionsDisposable.dispose();
  }
}
