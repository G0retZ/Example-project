package com.cargopull.executor_driver.presentation.orderconfirmation;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида бездействия подтверждения заказа.
 */
final class OrderConfirmationViewStateIdle implements ViewState<OrderConfirmationViewActions> {

  @NonNull
  private final OrderConfirmationTimeoutItem orderConfirmationTimeoutItem;

  OrderConfirmationViewStateIdle(
      @NonNull OrderConfirmationTimeoutItem orderConfirmationTimeoutItem) {
    this.orderConfirmationTimeoutItem = orderConfirmationTimeoutItem;
  }

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(false);
    stateActions.enableAcceptButton(true);
    stateActions.enableDeclineButton(true);
    stateActions.showAcceptedMessage(null);
    stateActions.showDeclinedMessage(null);
    stateActions.showFailedMessage(null);
    stateActions.showTimeout(
        orderConfirmationTimeoutItem.getProgressLeft(),
        orderConfirmationTimeoutItem.getTimeout()
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderConfirmationViewStateIdle that = (OrderConfirmationViewStateIdle) o;

    return orderConfirmationTimeoutItem.equals(that.orderConfirmationTimeoutItem);
  }

  @Override
  public int hashCode() {
    return orderConfirmationTimeoutItem.hashCode();
  }
}
