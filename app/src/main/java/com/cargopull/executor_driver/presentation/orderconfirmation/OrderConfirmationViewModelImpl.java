package com.cargopull.executor_driver.presentation.orderconfirmation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class OrderConfirmationViewModelImpl extends ViewModel implements
    OrderConfirmationViewModel {

  @NonNull
  private final OrderConfirmationUseCase orderConfirmationUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderConfirmationViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private Disposable decisionDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable timeoutDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<OrderConfirmationViewActions> lastViewState;

  @Inject
  public OrderConfirmationViewModelImpl(
      @NonNull OrderConfirmationUseCase orderConfirmationUseCase,
      @NonNull TimeUtils timeUtils) {
    this.orderConfirmationUseCase = orderConfirmationUseCase;
    this.timeUtils = timeUtils;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadOrderTimeout();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderConfirmationViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void acceptOrder() {
    if (!decisionDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OrderConfirmationViewStatePending());
    decisionDisposable = orderConfirmationUseCase.sendDecision(true)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> viewStateLiveData.postValue(new OrderConfirmationViewStateResult(message)),
            t -> {
              if (t instanceof OrderOfferExpiredException) {
                viewStateLiveData.postValue(new OrderConfirmationViewStateResult(t.getMessage()));
              } else if (t instanceof OrderOfferDecisionException) {
                viewStateLiveData.postValue(lastViewState);
              } else if (t instanceof DataMappingException) {
                viewStateLiveData.postValue(lastViewState);
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              } else {
                viewStateLiveData.postValue(lastViewState);
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              }
            }
        );
  }

  @Override
  public void declineOrder() {
    if (!decisionDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OrderConfirmationViewStatePending());
    decisionDisposable = orderConfirmationUseCase.sendDecision(false)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> navigateLiveData.postValue(OrderConfirmationNavigate.CLOSE),
            t -> {
              if (t instanceof OrderOfferExpiredException) {
                viewStateLiveData.postValue(new OrderConfirmationViewStateResult(t.getMessage()));
              } else if (t instanceof OrderOfferDecisionException) {
                viewStateLiveData.postValue(lastViewState);
              } else if (t instanceof DataMappingException) {
                viewStateLiveData.postValue(lastViewState);
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              } else {
                viewStateLiveData.postValue(lastViewState);
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              }
            }
        );
  }

  @Override
  public void counterTimeOut() {
    viewStateLiveData.postValue(new OrderConfirmationViewStatePending());
  }

  @Override
  public void messageConsumed() {
    navigateLiveData.postValue(OrderConfirmationNavigate.CLOSE);
  }

  private void loadOrderTimeout() {
    if (timeoutDisposable.isDisposed()) {
      viewStateLiveData.postValue(new OrderConfirmationViewStatePending());
      timeoutDisposable = orderConfirmationUseCase.getOrderDecisionTimeout()
          .observeOn(AndroidSchedulers.mainThread())
          .retry(throwable -> throwable instanceof OrderOfferExpiredException
              || throwable instanceof OrderOfferDecisionException)
          .subscribe(timeout -> viewStateLiveData.postValue(
              lastViewState = new OrderConfirmationViewStateIdle(
                  new OrderConfirmationTimeoutItem(timeout, timeUtils))
              ),
              throwable -> {
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
    decisionDisposable.dispose();
    timeoutDisposable.dispose();
  }
}
