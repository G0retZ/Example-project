package com.cargopull.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида заказа.
 */
final class OrderConfirmationViewStateIdle implements ViewState<OrderConfirmationViewActions> {

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(false);
    stateActions.enableAcceptButton(true);
    stateActions.enableDeclineButton(true);
    stateActions.showBlockingMessage(null);
  }
}
