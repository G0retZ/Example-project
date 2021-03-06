package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class OrderConfirmationViewStatePending implements ViewState<OrderConfirmationViewActions> {

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(true);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
    stateActions.showAcceptedMessage(null);
    stateActions.showDeclinedMessage(null);
    stateActions.showFailedMessage(null);
    stateActions.showTimeout(-1);
  }
}
