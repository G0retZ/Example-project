package com.cargopull.executor_driver.presentation.movingtoclienttimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class MovingToClientTimerViewModelImpl extends ViewModel implements
    MovingToClientTimerViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable timerDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<ViewActions> lastViewState;

  @Inject
  public MovingToClientTimerViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull OrderUseCase orderUseCase,
      @NonNull TimeUtils timeUtils) {
    this.errorReporter = errorReporter;
    this.orderUseCase = orderUseCase;
    this.timeUtils = timeUtils;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadOrders();
  }

  @NonNull
  @Override
  public LiveData<ViewState<ViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void loadOrders() {
    if (disposable.isDisposed()) {
      viewStateLiveData.postValue(new MovingToClientTimerViewStatePending(lastViewState));
      disposable = orderUseCase.getOrders()
          .observeOn(AndroidSchedulers.mainThread())
          .doOnError(throwable -> {
            timerDisposable.dispose();
            viewStateLiveData.postValue(
                lastViewState = new MovingToClientTimerViewStateCounting(0)
            );
          })
          .retry(throwable -> throwable instanceof OrderOfferExpiredException
              || throwable instanceof OrderOfferDecisionException
              || throwable instanceof OrderCancelledException)
          .subscribe(this::consumeOrder,
              throwable -> {
                errorReporter.reportError(throwable);
                if (throwable instanceof DataMappingException) {
                  navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
                }
              }
          );
    }
  }

  private void consumeOrder(@NonNull Order order) {
    timerDisposable.dispose();
    long start =
        timeUtils.currentTimeMillis() - order.getConfirmationTime() - order.getEtaToStartPoint();
    long amount = Math.round((order.getEtaToStartPoint() - start) / 1000d);
    timerDisposable = Observable.intervalRange(0, Math.max(amount, 600), 0, 1, TimeUnit.SECONDS)
        .map(aLong -> start + aLong * 1000)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            count -> viewStateLiveData.postValue(
                lastViewState = new MovingToClientTimerViewStateCounting(-count)
            ),
            throwable -> {
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
    timerDisposable.dispose();
  }
}
