package com.fasten.executor_driver.presentation.splahscreen;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.net.SocketTimeoutException;
import javax.inject.Inject;

public class SplashScreenViewModelImpl extends ViewModel implements SplashScreenViewModel {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final MutableLiveData<ViewState<SplashScreenViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @Nullable
  private Disposable disposable;

  @Inject
  public SplashScreenViewModelImpl(@NonNull ExecutorStateUseCase executorStateUseCase) {
    this.executorStateUseCase = executorStateUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new SplashScreenViewStatePending());
    navigateLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<SplashScreenViewActions>> getViewStateLiveData() {
    loadState();
    return viewStateLiveData;
  }

  private void loadState() {
    if (disposable == null || disposable.isDisposed()) {
      disposable = executorStateUseCase.loadStatus()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              () -> viewStateLiveData.postValue(new SplashScreenViewStateDone()),
              throwable -> {
                if (throwable instanceof NoNetworkException) {
                  viewStateLiveData.postValue(new SplashScreenViewStateNetworkError());
                } else if (throwable instanceof SocketTimeoutException) {
                  viewStateLiveData.postValue(new SplashScreenViewStateNetworkError());
                } else {
                  throw new RuntimeException(throwable);
                }
              });
    }
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
