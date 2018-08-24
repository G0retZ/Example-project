package com.cargopull.executor_driver.presentation.executorstate;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class ExecutorStateViewModelImpl extends ViewModel implements ExecutorStateViewModel {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ExecutorStateViewActions>> messageLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public ExecutorStateViewModelImpl(@NonNull ExecutorStateUseCase executorStateUseCase) {
    this.executorStateUseCase = executorStateUseCase;
    messageLiveData = new MutableLiveData<>();
    navigateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<ExecutorStateViewActions>> getViewStateLiveData() {
    return messageLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void messageConsumed() {
    messageLiveData.postValue(null);
  }

  @Override
  public void initializeExecutorState() {
    disposable.dispose();
    disposable = executorStateUseCase.getExecutorStates()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            executorState -> {
              switch (executorState) {
                case SHIFT_CLOSED:
                  navigateLiveData.postValue(ExecutorStateNavigate.MAP_SHIFT_CLOSED);
                  break;
                case SHIFT_OPENED:
                  if (executorState.getData() != null
                      && !executorState.getData().trim().isEmpty()) {
                    messageLiveData.postValue(
                        executorStateViewActions -> executorStateViewActions
                            .showOnlineMessage(executorState.getData())
                    );
                  }
                  navigateLiveData.postValue(ExecutorStateNavigate.MAP_SHIFT_OPENED);
                  break;
                case ONLINE:
                  if (executorState.getData() != null
                      && !executorState.getData().trim().isEmpty()) {
                    messageLiveData.postValue(
                        executorStateViewActions -> executorStateViewActions
                            .showOnlineMessage(executorState.getData())
                    );
                  }
                  navigateLiveData.postValue(ExecutorStateNavigate.MAP_ONLINE);
                  break;
                case DRIVER_ORDER_CONFIRMATION:
                  navigateLiveData.postValue(ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION);
                  break;
                case CLIENT_ORDER_CONFIRMATION:
                  navigateLiveData.postValue(ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION);
                  break;
                case MOVING_TO_CLIENT:
                  navigateLiveData.postValue(ExecutorStateNavigate.MOVING_TO_CLIENT);
                  break;
                case WAITING_FOR_CLIENT:
                  navigateLiveData.postValue(ExecutorStateNavigate.WAITING_FOR_CLIENT);
                  break;
                case ORDER_FULFILLMENT:
                  navigateLiveData.postValue(ExecutorStateNavigate.ORDER_FULFILLMENT);
                  break;
                case PAYMENT_CONFIRMATION:
                  navigateLiveData.postValue(ExecutorStateNavigate.PAYMENT_CONFIRMATION);
                  break;
              }
            },
            throwable -> {
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              }
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
