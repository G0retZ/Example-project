package com.fasten.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки сети вида заказа.
 */
final class OrderConfirmationViewStateNetworkError extends OrderConfirmationViewState {

  OrderConfirmationViewStateNetworkError(@Nullable OrderConfirmationItem orderConfirmationItem) {
    super(orderConfirmationItem);
  }

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderConfirmationPending(false);
    stateActions.enableDeclineButton(false);
    stateActions.showOrderConfirmationAvailabilityError(false);
    stateActions.showOrderConfirmationNetworkErrorMessage(true);
  }
}
