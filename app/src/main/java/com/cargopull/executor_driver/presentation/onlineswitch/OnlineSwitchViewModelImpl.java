package com.cargopull.executor_driver.presentation.onlineswitch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateNotOnlineUseCase;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class OnlineSwitchViewModelImpl extends ViewModel implements OnlineSwitchViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ExecutorStateNotOnlineUseCase executorStateNotOnlineUseCase;
  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OnlineSwitchViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable executorStatesDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable setStateDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<OnlineSwitchViewActions> lastViewState;

  @Inject
  public OnlineSwitchViewModelImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull ExecutorStateNotOnlineUseCase executorStateNotOnlineUseCase,
      @NonNull ExecutorStateUseCase executorStateUseCase) {
    this.errorReporter = errorReporter;
    this.executorStateNotOnlineUseCase = executorStateNotOnlineUseCase;
    this.executorStateUseCase = executorStateUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadExecutorState();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OnlineSwitchViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void setNewState(boolean online) {
    if (setStateDisposable.isDisposed()) {
      if (online) {
        viewStateLiveData.postValue(lastViewState = new OnlineSwitchViewState(false));
        navigateLiveData.postValue(OnlineSwitchNavigate.VEHICLE_OPTIONS);
      } else {
        viewStateLiveData
            .postValue(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
        setStateDisposable = executorStateNotOnlineUseCase.setExecutorNotOnline()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                () -> {
                },
                throwable -> {
                  errorReporter.reportError(throwable);
                  viewStateLiveData.postValue(new OnlineSwitchViewState(true));
                  navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
                });
      }
    }
  }

  private void loadExecutorState() {
    viewStateLiveData.postValue(new OnlineSwitchViewStatePending(lastViewState));
    executorStatesDisposable.dispose();
    executorStatesDisposable = executorStateUseCase.getExecutorStates()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onNextState,
            throwable -> {
              errorReporter.reportError(throwable);
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              }
            }
        );
  }

  private void onNextState(ExecutorState executorState) {
    switch (executorState) {
      case BLOCKED:
        lastViewState = new OnlineSwitchViewState(false);
        break;
      case SHIFT_CLOSED:
        lastViewState = new OnlineSwitchViewState(false);
        break;
      case SHIFT_OPENED:
        lastViewState = new OnlineSwitchViewState(false);
        break;
      case ONLINE:
        lastViewState = new OnlineSwitchViewState(true);
        break;
      case DRIVER_ORDER_CONFIRMATION:
        lastViewState = new OnlineSwitchViewState(true);
        break;
      case DRIVER_PRELIMINARY_ORDER_CONFIRMATION:
        lastViewState = new OnlineSwitchViewState(true);
        break;
      case CLIENT_ORDER_CONFIRMATION:
        lastViewState = new OnlineSwitchViewState(true);
        break;
      case MOVING_TO_CLIENT:
        lastViewState = new OnlineSwitchViewState(true);
        break;
      case WAITING_FOR_CLIENT:
        lastViewState = new OnlineSwitchViewState(true);
        break;
      case ORDER_FULFILLMENT:
        lastViewState = new OnlineSwitchViewState(true);
        break;
      case PAYMENT_CONFIRMATION:
        lastViewState = new OnlineSwitchViewState(true);
        break;
      default:
    }
    viewStateLiveData.postValue(lastViewState);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    setStateDisposable.dispose();
    executorStatesDisposable.dispose();
  }
}
