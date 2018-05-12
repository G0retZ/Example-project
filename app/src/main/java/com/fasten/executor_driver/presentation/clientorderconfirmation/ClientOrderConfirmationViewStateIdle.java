package com.fasten.executor_driver.presentation.clientorderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние бездействия вида заказа.
 */
final class ClientOrderConfirmationViewStateIdle extends
    ClientOrderConfirmationViewState {

  ClientOrderConfirmationViewStateIdle(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull ClientOrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showPending(false);
    stateActions.enableDeclineButton(true);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
