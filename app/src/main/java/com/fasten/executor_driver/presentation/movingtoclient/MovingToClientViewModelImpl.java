package com.fasten.executor_driver.presentation.movingtoclient;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.MovingToClientUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class MovingToClientViewModelImpl extends ViewModel implements MovingToClientViewModel {

  @NonNull
  private final MovingToClientUseCase movingToClientUseCase;
  @NonNull
  private final MutableLiveData<ViewState<MovingToClientViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable callingDisposable = EmptyDisposable.INSTANCE;

  @Inject
  public MovingToClientViewModelImpl(@NonNull MovingToClientUseCase movingToClientUseCase) {
    this.movingToClientUseCase = movingToClientUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new MovingToClientViewStateIdle());
  }

  @NonNull
  @Override
  public LiveData<ViewState<MovingToClientViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void callToClient() {
    if (!callingDisposable.isDisposed()) {
      return;
    }
    callingDisposable = Completable.complete()
        .doOnComplete(() -> {
          viewStateLiveData.postValue(new MovingToClientViewStateCalling());
          navigateLiveData.postValue(MovingToClientNavigate.CALL_TO_CLIENT);
        })
        .delay(10, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> viewStateLiveData.postValue(new MovingToClientViewStateIdle()), throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new MovingToClientViewStateIdle());
            }
        );
  }

  @Override
  public void reportArrival() {
    if (!disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new MovingToClientViewStatePending());
    disposable = movingToClientUseCase.reportArrival()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new MovingToClientViewStateIdle());
              navigateLiveData.postValue(MovingToClientNavigate.NO_CONNECTION);
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (!disposable.isDisposed()) {
      disposable.dispose();
    }
    callingDisposable.dispose();
  }
}
