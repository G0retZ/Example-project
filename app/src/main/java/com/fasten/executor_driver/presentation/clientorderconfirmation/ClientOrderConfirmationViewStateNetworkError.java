package com.fasten.executor_driver.presentation.clientorderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки сети вида заказа.
 */
final class ClientOrderConfirmationViewStateNetworkError extends ClientOrderConfirmationViewState {

  ClientOrderConfirmationViewStateNetworkError(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull ClientOrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showPending(false);
    stateActions.enableDeclineButton(false);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(true);
  }
}
