package com.fasten.executor_driver.presentation.driverorderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки доступности зака вида заказа.
 */
final class DriverOrderConfirmationViewStateUnavailableError extends
    DriverOrderConfirmationViewState {

  DriverOrderConfirmationViewStateUnavailableError(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull DriverOrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showPending(false);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
    stateActions.showOrderAvailabilityError(true);
    stateActions.showNetworkErrorMessage(false);
  }
}
