package com.fasten.executor_driver.presentation.onlineswitch;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.interactor.ExecutorStateNotOnlineUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

class OnlineSwitchViewModelImpl extends ViewModel implements OnlineSwitchViewModel {

  @NonNull
  private final ExecutorStateNotOnlineUseCase executorStateNotOnlineUseCase;
  @NonNull
  private final SingleLiveEvent<ViewState<OnlineSwitchViewActions>> viewStateLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable executorStatesDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable setStateDisposable = EmptyDisposable.INSTANCE;

  @Inject
  OnlineSwitchViewModelImpl(@NonNull ExecutorStateNotOnlineUseCase executorStateNotOnlineUseCase) {
    this.executorStateNotOnlineUseCase = executorStateNotOnlineUseCase;
    viewStateLiveData = new SingleLiveEvent<>();
    navigateLiveData = new MutableLiveData<>();
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
        viewStateLiveData.postValue(new OnlineSwitchViewStateUnCheckedRegular());
        navigateLiveData.postValue(OnlineSwitchNavigate.VEHICLE_OPTIONS);
      } else {
        viewStateLiveData.postValue(new OnlineSwitchViewStateUnCheckedPending());
        setStateDisposable = executorStateNotOnlineUseCase.setExecutorNotOnline()
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                () -> {
                },
                throwable -> viewStateLiveData
                    .postValue(new OnlineSwitchViewStateCheckedServerError()));
      }
    }
  }

  @Override
  public void refreshStates() {
    loadExecutorState();
  }

  private void loadExecutorState() {
    viewStateLiveData.postValue(new OnlineSwitchViewStateUnCheckedPending());
    executorStatesDisposable.dispose();
    executorStatesDisposable = executorStateNotOnlineUseCase.getExecutorStates()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onNextState, throwable -> onStateError());
  }

  private void onNextState(ExecutorState executorState) {
    switch (executorState) {
      case SHIFT_CLOSED:
        viewStateLiveData.postValue(new OnlineSwitchViewStateUnCheckedPending());
        break;
      case SHIFT_OPENED:
        viewStateLiveData.postValue(new OnlineSwitchViewStateUnCheckedRegular());
        break;
      case ONLINE:
        viewStateLiveData.postValue(new OnlineSwitchViewStateCheckedRegular());
        break;
      case ORDER_CONFIRMATION:
        viewStateLiveData.postValue(new OnlineSwitchViewStateUnCheckedPending());
        break;
      case IN_PROGRESS:
        viewStateLiveData.postValue(new OnlineSwitchViewStateUnCheckedPending());
        break;
      default:
    }
  }

  private void onStateError() {
    viewStateLiveData.postValue(new OnlineSwitchViewStateUnCheckedSocketError());
  }

  @Override
  public void consumeSocketError() {
    viewStateLiveData.postValue(new OnlineSwitchViewStateCheckedRegular());
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    setStateDisposable.dispose();
    executorStatesDisposable.dispose();
  }
}
