package com.fasten.executor_driver.presentation.driverorderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки сети вида заказа.
 */
final class DriverOrderConfirmationViewStateNetworkError extends DriverOrderConfirmationViewState {

  DriverOrderConfirmationViewStateNetworkError(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull DriverOrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showPending(false);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
    stateActions.showOfferAvailabilityError(false);
    stateActions.showNetworkErrorMessage(true);
  }
}
