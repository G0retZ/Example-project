package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида результата истечения срока заказа.
 */
final class OrderConfirmationViewStateExpired implements ViewState<OrderConfirmationViewActions> {

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(false);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
    stateActions.showAcceptedMessage(null);
    stateActions.showDeclinedMessage(null);
    stateActions.showFailedMessage(null);
  }
}
