package com.cargopull.executor_driver.presentation.onlineswitch;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateNotOnlineUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OnlineSwitchViewModelImpl extends ViewModel implements OnlineSwitchViewModel {

  @NonNull
  private final ExecutorStateNotOnlineUseCase executorStateNotOnlineUseCase;
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
      @NonNull ExecutorStateNotOnlineUseCase executorStateNotOnlineUseCase) {
    this.executorStateNotOnlineUseCase = executorStateNotOnlineUseCase;
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
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                () -> {
                },
                throwable -> {
                  throwable.printStackTrace();
                  if (throwable instanceof IllegalStateException) {
                    viewStateLiveData.postValue(new OnlineSwitchViewState(true));
                    navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
                  } else {
                    navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
                  }
                });
      }
    }
  }

  private void loadExecutorState() {
    viewStateLiveData.postValue(new OnlineSwitchViewStatePending(lastViewState));
    executorStatesDisposable.dispose();
    executorStatesDisposable = executorStateNotOnlineUseCase.getExecutorStates()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onNextState, throwable -> {
          throwable.printStackTrace();
          navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
        });
  }

  private void onNextState(ExecutorState executorState) {
    switch (executorState) {
      case SHIFT_CLOSED:
        viewStateLiveData
            .postValue(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(false)));
        break;
      case SHIFT_OPENED:
        viewStateLiveData.postValue(lastViewState = new OnlineSwitchViewState(false));
        break;
      case ONLINE:
        viewStateLiveData.postValue(lastViewState = new OnlineSwitchViewState(true));
        break;
      case DRIVER_ORDER_CONFIRMATION:
        viewStateLiveData
            .postValue(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
        break;
      case CLIENT_ORDER_CONFIRMATION:
        viewStateLiveData
            .postValue(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
        break;
      case MOVING_TO_CLIENT:
        viewStateLiveData
            .postValue(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
        break;
      case WAITING_FOR_CLIENT:
        viewStateLiveData
            .postValue(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
        break;
      case ORDER_FULFILLMENT:
        viewStateLiveData
            .postValue(new OnlineSwitchViewStatePending(new OnlineSwitchViewState(true)));
        break;
      default:
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    setStateDisposable.dispose();
    executorStatesDisposable.dispose();
  }
}
