package com.fasten.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки сети вида заказа.
 */
final class OrderConfirmationViewStateError implements ViewState<OrderConfirmationViewActions> {

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(false);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(true);
  }
}
