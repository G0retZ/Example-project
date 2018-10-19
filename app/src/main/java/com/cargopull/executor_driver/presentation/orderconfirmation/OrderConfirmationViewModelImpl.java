package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException;
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
import java.util.HashMap;
import javax.inject.Inject;
import retrofit2.HttpException;

public class OrderConfirmationViewModelImpl extends ViewModel implements
    OrderConfirmationViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderConfirmationUseCase orderConfirmationUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderConfirmationViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private final TimeUtils timeUtils;
  @Nullable
  private final EventLogger eventLogger;
  @NonNull
  private Disposable decisionDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable timeoutDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<OrderConfirmationViewActions> lastViewState;
  private long timeStamp;
  private long orderId;

  @Inject
  public OrderConfirmationViewModelImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull OrderConfirmationUseCase orderConfirmationUseCase,
      @NonNull TimeUtils timeUtils,
      @Nullable EventLogger eventLogger) {
    this.errorReporter = errorReporter;
    this.orderConfirmationUseCase = orderConfirmationUseCase;
    this.timeUtils = timeUtils;
    this.eventLogger = eventLogger;
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
            message -> {
              if (eventLogger != null) {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(orderId));
                params.put("decision_duration",
                    String.valueOf(timeUtils.currentTimeMillis() - timeStamp));
                eventLogger.reportEvent("order_offer_accepted", params);
              }
              viewStateLiveData.postValue(new OrderConfirmationViewStateAccepted(message));
            },
            t -> {
              if (t instanceof OrderConfirmationFailedException) {
                viewStateLiveData.postValue(new OrderConfirmationViewStateFailed(t.getMessage()));
              } else if (t instanceof OrderOfferExpiredException) {
                viewStateLiveData.postValue(lastViewState);
              } else if (t instanceof OrderOfferDecisionException) {
                viewStateLiveData.postValue(lastViewState);
              } else if (t instanceof DataMappingException) {
                errorReporter.reportError(t);
                viewStateLiveData.postValue(lastViewState);
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              } else {
                if (!(t instanceof HttpException)) {
                  errorReporter.reportError(t);
                }
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
            message -> {
              if (eventLogger != null) {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(orderId));
                params.put("decision_duration",
                    String.valueOf(timeUtils.currentTimeMillis() - timeStamp));
                eventLogger.reportEvent("order_offer_declined", params);
              }
              viewStateLiveData.postValue(new OrderConfirmationViewStateDeclined(message));
            },
            t -> {
              if (t instanceof OrderConfirmationFailedException) {
                viewStateLiveData.postValue(new OrderConfirmationViewStateFailed(t.getMessage()));
              } else if (t instanceof OrderOfferExpiredException) {
                viewStateLiveData.postValue(lastViewState);
              } else if (t instanceof OrderOfferDecisionException) {
                viewStateLiveData.postValue(lastViewState);
              } else if (t instanceof DataMappingException) {
                errorReporter.reportError(t);
                viewStateLiveData.postValue(lastViewState);
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              } else {
                if (!(t instanceof HttpException)) {
                  errorReporter.reportError(t);
                }
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
          .doOnError(throwable -> {
            if (throwable instanceof OrderOfferExpiredException) {
              viewStateLiveData.postValue(
                  new OrderConfirmationViewStateExpired()
              );
            }
          })
          .retry(throwable -> throwable instanceof OrderOfferExpiredException
              || throwable instanceof OrderOfferDecisionException)
          .subscribe(pair -> {
                OrderConfirmationTimeoutItem orderConfirmationTimeoutItem =
                    new OrderConfirmationTimeoutItem(pair.second, timeUtils);
                orderId = pair.first;
                timeStamp = orderConfirmationTimeoutItem.getItemTimestamp();
                viewStateLiveData.postValue(
                    lastViewState = new OrderConfirmationViewStateIdle(orderConfirmationTimeoutItem)
                );
              },
              throwable -> {
                if (!(throwable instanceof HttpException)) {
                  errorReporter.reportError(throwable);
                }
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
