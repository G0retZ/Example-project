package com.fasten.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class OrderConfirmationViewStatePending implements ViewState<OrderConfirmationViewActions> {

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(true);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
  }
}
