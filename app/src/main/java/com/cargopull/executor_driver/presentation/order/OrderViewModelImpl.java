package com.cargopull.executor_driver.presentation.order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class OrderViewModelImpl extends ViewModel implements
    OrderViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<OrderViewActions> lastViewState;

  @Inject
  public OrderViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull OrderUseCase orderUseCase) {
    this.errorReporter = errorReporter;
    this.orderUseCase = orderUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadOrders();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void loadOrders() {
    if (disposable.isDisposed()) {
      viewStateLiveData.postValue(new OrderViewStatePending(lastViewState));
      disposable = orderUseCase.getOrders()
          .observeOn(AndroidSchedulers.mainThread())
          .doOnError(throwable -> {
            if (throwable instanceof OrderOfferExpiredException) {
              viewStateLiveData.postValue(
                  new OrderViewStateExpired(
                      lastViewState,
                      throwable.getMessage(),
                      () -> navigateLiveData.postValue(OrderNavigate.CLOSE)
                  )
              );
            } else if (throwable instanceof OrderCancelledException) {
              viewStateLiveData.postValue(
                  new OrderViewStateCancelled(
                      lastViewState,
                      () -> navigateLiveData.postValue(OrderNavigate.CLOSE)
                  )
              );
            }
          })
          .retry(throwable -> throwable instanceof OrderOfferExpiredException
              || throwable instanceof OrderOfferDecisionException
              || throwable instanceof OrderCancelledException)
          .subscribe(
              order -> viewStateLiveData.postValue(lastViewState = new OrderViewStateIdle(order)),
              throwable -> {
                errorReporter.reportError(throwable);
                if (throwable instanceof DataMappingException) {
                  navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
                }
              }
          );
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
