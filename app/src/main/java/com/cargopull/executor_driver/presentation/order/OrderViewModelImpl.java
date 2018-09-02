package com.cargopull.executor_driver.presentation.order;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class OrderViewModelImpl extends ViewModel implements
    OrderViewModel {

  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<OrderViewActions> lastViewState;

  @Inject
  public OrderViewModelImpl(@NonNull OrderUseCase orderUseCase, @NonNull TimeUtils timeUtils) {
    this.orderUseCase = orderUseCase;
    this.timeUtils = timeUtils;
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
                  new OrderViewStateExpired(lastViewState, throwable.getMessage())
              );
            }
          })
          .retry(throwable -> throwable instanceof OrderOfferExpiredException
              || throwable instanceof OrderOfferDecisionException)
          .subscribe(this::consumeOrder,
              throwable -> {
                if (throwable instanceof DataMappingException) {
                  navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
                }
              }
          );
    }
  }


  private void consumeOrder(@NonNull Order order) {
    lastViewState = new OrderViewStateIdle(new OrderItem(order, timeUtils));
    viewStateLiveData.postValue(lastViewState);
  }

  @Override
  public void messageConsumed() {
    navigateLiveData.postValue(OrderNavigate.CLOSE);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
