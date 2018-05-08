package com.fasten.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class OrderConfirmationViewStatePending extends OrderConfirmationViewState {

  OrderConfirmationViewStatePending(@Nullable OrderConfirmationItem orderConfirmationItem) {
    super(orderConfirmationItem);
  }

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderConfirmationPending(true);
    stateActions.enableDeclineButton(false);
    stateActions.showOrderConfirmationAvailabilityError(false);
    stateActions.showOrderConfirmationNetworkErrorMessage(false);
  }
}
