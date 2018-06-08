package com.fasten.executor_driver.presentation.balance;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.interactor.ExecutorBalanceUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

class BalanceViewModelImpl extends ViewModel implements BalanceViewModel {

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
  BalanceViewModelImpl(@NonNull ExecutorBalanceUseCase executorBalanceUseCase) {
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
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            executorBalance -> viewStateLiveData.postValue(new BalanceViewState(executorBalance)),
            throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new BalanceViewStateError(lastViewState));
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
