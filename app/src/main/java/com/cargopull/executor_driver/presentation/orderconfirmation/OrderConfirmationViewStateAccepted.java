package com.cargopull.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида результата успешного подтверждения заказа.
 */
final class OrderConfirmationViewStateAccepted implements ViewState<OrderConfirmationViewActions> {

  @NonNull
  private final String message;

  OrderConfirmationViewStateAccepted(@NonNull String message) {
    this.message = message;
  }

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(true);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
    stateActions.showDeclinedMessage(null);
    stateActions.showFailedMessage(null);
    stateActions.showAcceptedMessage(message);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderConfirmationViewStateAccepted that = (OrderConfirmationViewStateAccepted) o;

    return message.equals(that.message);
  }

  @Override
  public int hashCode() {
    return message.hashCode();
  }
}
