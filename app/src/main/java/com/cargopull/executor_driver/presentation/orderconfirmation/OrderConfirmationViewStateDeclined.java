package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида результата успешного отказа от заказа.
 */
final class OrderConfirmationViewStateDeclined implements ViewState<OrderConfirmationViewActions> {

  @NonNull
  private final String message;

  OrderConfirmationViewStateDeclined(@NonNull String message) {
    this.message = message;
  }

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(true);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
    stateActions.showAcceptedMessage(null);
    stateActions.showFailedMessage(null);
    stateActions.showDeclinedMessage(message);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderConfirmationViewStateDeclined that = (OrderConfirmationViewStateDeclined) o;

    return message.equals(that.message);
  }

  @Override
  public int hashCode() {
    return message.hashCode();
  }
}
