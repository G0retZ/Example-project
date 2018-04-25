package com.fasten.executor_driver.presentation.executorstate;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.AuthorizationException;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ExecutorStateViewModelImpl extends ViewModel implements ExecutorStateViewModel {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ExecutorStateViewActions>> viewStateLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public ExecutorStateViewModelImpl(@NonNull ExecutorStateUseCase executorStateUseCase) {
    this.executorStateUseCase = executorStateUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<ExecutorStateViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void initializeExecutorState(boolean reset) {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = executorStateUseCase.getExecutorStates(reset)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            executorState -> {
              switch (executorState) {
                case SHIFT_CLOSED:
                  navigateLiveData.postValue(ExecutorStateNavigate.MAP_SHIFT_CLOSED);
                  break;
                case SHIFT_OPENED:
                  navigateLiveData.postValue(ExecutorStateNavigate.MAP_SHIFT_OPENED);
                  break;
                case ONLINE:
                  navigateLiveData.postValue(ExecutorStateNavigate.MAP_ONLINE);
                  break;
                case ORDER_CONFIRMATION:
                  navigateLiveData.postValue(ExecutorStateNavigate.OFFER_CONFIRMATION);
                  break;
                case IN_PROGRESS:
                  navigateLiveData.postValue(ExecutorStateNavigate.APPROACHING_LOAD_POINT);
                  break;
              }
            },
            throwable -> {
              if ((throwable instanceof AuthorizationException)) {
                navigateLiveData.postValue(ExecutorStateNavigate.AUTHORIZE);
              } else {
                navigateLiveData.postValue(ExecutorStateNavigate.NO_NETWORK);
              }
            });
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
