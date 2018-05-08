package com.fasten.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки доступности зака вида заказа.
 */
final class OrderConfirmationViewStateUnavailableError extends OrderConfirmationViewState {

  OrderConfirmationViewStateUnavailableError(
      @Nullable OrderConfirmationItem orderConfirmationItem) {
    super(orderConfirmationItem);
  }

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderConfirmationPending(false);
    stateActions.enableDeclineButton(false);
    stateActions.showOrderConfirmationAvailabilityError(true);
    stateActions.showOrderConfirmationNetworkErrorMessage(false);
  }
}
