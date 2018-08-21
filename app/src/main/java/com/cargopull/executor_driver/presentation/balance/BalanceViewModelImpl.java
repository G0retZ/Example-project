package com.cargopull.executor_driver.presentation.balance;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorBalanceUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class BalanceViewModelImpl extends ViewModel implements BalanceViewModel {

  @NonNull
  private final ExecutorBalanceUseCase executorBalanceUseCase;
  @NonNull
  private final MutableLiveData<ViewState<BalanceViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<BalanceViewActions> lastViewState;

  @Inject
  public BalanceViewModelImpl(@NonNull ExecutorBalanceUseCase executorBalanceUseCase) {
    this.executorBalanceUseCase = executorBalanceUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadBalance();
  }

  @NonNull
  @Override
  public LiveData<ViewState<BalanceViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void loadBalance() {
    if (!disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new BalanceViewStatePending(lastViewState));
    disposable = executorBalanceUseCase.getExecutorBalance(false)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            executorBalance -> viewStateLiveData
                .postValue(lastViewState = new BalanceViewState(executorBalance)),
            throwable -> {
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              }
            }
        );
  }

  @Override
  public void replenishAccount() {
    navigateLiveData.postValue(BalanceNavigate.PAYMENT_OPTIONS);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
