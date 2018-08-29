package com.cargopull.executor_driver.presentation.calltoclient;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.CallToClientUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class CallToClientViewModelImpl extends ViewModel implements CallToClientViewModel {

  @NonNull
  private final CallToClientUseCase callToClientUseCase;
  @NonNull
  private final MutableLiveData<ViewState<CallToClientViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public CallToClientViewModelImpl(@NonNull CallToClientUseCase callToClientUseCase) {
    this.callToClientUseCase = callToClientUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new CallToClientViewStateNotCalling());
  }

  @NonNull
  @Override
  public LiveData<ViewState<CallToClientViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void callToClient() {
    if (!disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new CallToClientViewStatePending());
    disposable = callToClientUseCase.callToClient()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete(() -> viewStateLiveData.postValue(new CallToClientViewStateCalling()))
        .delay(10, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> viewStateLiveData.postValue(new CallToClientViewStateNotCalling()),
            throwable -> {
              viewStateLiveData.postValue(new CallToClientViewStateNotCalling());
              navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
