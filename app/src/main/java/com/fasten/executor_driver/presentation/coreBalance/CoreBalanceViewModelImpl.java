package com.fasten.executor_driver.presentation.coreBalance;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.ExecutorBalanceUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsNavigate;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CoreBalanceViewModelImpl extends ViewModel implements CoreBalanceViewModel {

  @NonNull
  private final ExecutorBalanceUseCase executorBalanceUseCase;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public CoreBalanceViewModelImpl(@NonNull ExecutorBalanceUseCase executorBalanceUseCase) {
    this.executorBalanceUseCase = executorBalanceUseCase;
    navigateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<CoreBalanceViewActions>> getViewStateLiveData() {
    return new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void initializeExecutorBalance(boolean reset) {
    disposable.dispose();
    disposable = executorBalanceUseCase.getExecutorBalance(reset)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            executorBalance -> {
            },
            throwable -> {
              throwable.printStackTrace();
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CancelOrderReasonsNavigate.SERVER_DATA_ERROR);
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
