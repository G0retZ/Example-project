package com.fasten.executor_driver.presentation.splahscreen;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.web.AuthorizationException;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class SplashScreenViewModelImpl extends ViewModel implements SplashScreenViewModel {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final MutableLiveData<ViewState<SplashScreenViewActions>> viewStateLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @Nullable
  private Disposable disposable;

  @Inject
  public SplashScreenViewModelImpl(@NonNull ExecutorStateUseCase executorStateUseCase) {
    this.executorStateUseCase = executorStateUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<SplashScreenViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void initializeApp() {
    if (disposable == null || disposable.isDisposed()) {
      disposable = executorStateUseCase.getExecutorStates()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              executorState -> {
                switch (executorState) {
                  case SHIFT_CLOSED:
                    navigateLiveData.postValue(SplashScreenNavigate.MAP_SHIFT_CLOSED);
                    break;
                  case SHIFT_OPENED:
                    navigateLiveData.postValue(SplashScreenNavigate.MAP_SHIFT_OPENED);
                    break;
                  case ONLINE:
                    navigateLiveData.postValue(SplashScreenNavigate.MAP_ONLINE);
                    break;
                }
              },
              throwable -> {
                if ((throwable instanceof AuthorizationException)) {
                  navigateLiveData.postValue(SplashScreenNavigate.AUTHORIZE);
                } else {
                  navigateLiveData.postValue(SplashScreenNavigate.NO_NETWORK);
                }
              });
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
