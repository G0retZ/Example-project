package com.cargopull.executor_driver.presentation.clientorderconfirmationtime;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

// TODO: https://jira.capsrv.xyz/browse/RUCAP-2122
public class ClientOrderConfirmationTimeViewModelImpl extends ViewModel implements
    ClientOrderConfirmationTimeViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ClientOrderConfirmationTimeViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public ClientOrderConfirmationTimeViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull ExecutorStateUseCase executorStateUseCase) {
    this.errorReporter = errorReporter;
    this.executorStateUseCase = executorStateUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new ClientOrderConfirmationTimeViewStateCounting(0));
    loadOrderTime();
  }

  @NonNull
  @Override
  public LiveData<ViewState<ClientOrderConfirmationTimeViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void loadOrderTime() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = executorStateUseCase.getExecutorStates()
        .observeOn(AndroidSchedulers.mainThread())
        .switchMap(executorState -> Flowable.interval(0, 1, TimeUnit.SECONDS)
            .map(count -> executorState.getCustomerTimer() - count * 1000)
            .takeUntil(count -> count <= 0)
        ).observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            time -> viewStateLiveData.postValue(
                time > 0 ? new ClientOrderConfirmationTimeViewStateCounting(time) :
                    new ClientOrderConfirmationTimeViewStateNotCounting()
            ),
            throwable -> {
              errorReporter.reportError(throwable);
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
