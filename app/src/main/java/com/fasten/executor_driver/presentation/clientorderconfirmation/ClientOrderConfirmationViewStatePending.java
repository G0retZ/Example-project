package com.fasten.executor_driver.presentation.clientorderconfirmation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class ClientOrderConfirmationViewStatePending extends ClientOrderConfirmationViewState {

  ClientOrderConfirmationViewStatePending(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull ClientOrderConfirmationViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showPending(true);
    stateActions.enableDeclineButton(false);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
