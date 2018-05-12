package com.fasten.executor_driver.presentation.clientorderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки доступности зака вида заказа.
 */
final class ClientOrderConfirmationViewStateUnavailableError extends
    ClientOrderConfirmationViewState {

  ClientOrderConfirmationViewStateUnavailableError(
      @Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull ClientOrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showClientOrderConfirmationPending(false);
    stateActions.enableDeclineButton(false);
    stateActions.showOrderAvailabilityError(true);
    stateActions.showNetworkErrorMessage(false);
  }
}
