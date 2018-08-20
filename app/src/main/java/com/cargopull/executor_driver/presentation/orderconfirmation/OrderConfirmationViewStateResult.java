package com.cargopull.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия результата запроса.
 */
final class OrderConfirmationViewStateResult implements ViewState<OrderConfirmationViewActions> {

  @NonNull
  private final String message;

  OrderConfirmationViewStateResult(@NonNull String message) {
    this.message = message;
  }

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(false);
    stateActions.enableAcceptButton(false);
    stateActions.enableDeclineButton(false);
    stateActions.showBlockingMessage(message);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderConfirmationViewStateResult that = (OrderConfirmationViewStateResult) o;

    return message.equals(that.message);
  }

  @Override
  public int hashCode() {
    return message.hashCode();
  }
}
