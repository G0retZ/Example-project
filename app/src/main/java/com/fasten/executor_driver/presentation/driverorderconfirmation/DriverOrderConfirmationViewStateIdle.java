package com.fasten.executor_driver.presentation.driverorderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние бездействия вида заказа.
 */
final class DriverOrderConfirmationViewStateIdle extends DriverOrderConfirmationViewState {

  DriverOrderConfirmationViewStateIdle(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull DriverOrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showPending(false);
    stateActions.enableAcceptButton(true);
    stateActions.enableDeclineButton(true);
    stateActions.showOfferAvailabilityError(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
