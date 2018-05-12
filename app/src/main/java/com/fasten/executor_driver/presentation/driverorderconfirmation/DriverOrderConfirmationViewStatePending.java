package com.fasten.executor_driver.presentation.driverorderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class DriverOrderConfirmationViewStatePending extends DriverOrderConfirmationViewState {

  DriverOrderConfirmationViewStatePending(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull DriverOrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showPending(true);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
