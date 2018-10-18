package com.cargopull.executor_driver.presentation.balance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
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
  private final ErrorReporter errorReporter;
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
  public BalanceViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull ExecutorBalanceUseCase executorBalanceUseCase) {
    this.errorReporter = errorReporter;
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
    disposable.dispose();
    viewStateLiveData.postValue(new BalanceViewStatePending(lastViewState));
    disposable = executorBalanceUseCase.getExecutorBalance()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            executorBalance -> viewStateLiveData
                .postValue(lastViewState = new BalanceViewState(executorBalance)),
            throwable -> {
              errorReporter.reportError(throwable);
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
