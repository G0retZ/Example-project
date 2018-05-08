package com.fasten.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние бездействия вида заказа.
 */
final class OrderConfirmationViewStateIdle extends
    OrderConfirmationViewState {

  OrderConfirmationViewStateIdle(@Nullable OrderConfirmationItem orderConfirmationItem) {
    super(orderConfirmationItem);
  }

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderConfirmationPending(false);
    stateActions.enableDeclineButton(true);
    stateActions.showOrderConfirmationAvailabilityError(false);
    stateActions.showOrderConfirmationNetworkErrorMessage(false);
  }
}
