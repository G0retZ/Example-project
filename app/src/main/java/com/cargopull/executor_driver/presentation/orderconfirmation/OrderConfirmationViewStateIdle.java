package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида бездействия подтверждения заказа.
 */
final class OrderConfirmationViewStateIdle implements ViewState<OrderConfirmationViewActions> {

  @NonNull
  private final OrderConfirmationTimeoutItem orderConfirmationTimeoutItem;
  private final boolean acceptEnabled;

  OrderConfirmationViewStateIdle(
      @NonNull OrderConfirmationTimeoutItem orderConfirmationTimeoutItem, boolean acceptEnabled) {
    this.orderConfirmationTimeoutItem = orderConfirmationTimeoutItem;
    this.acceptEnabled = acceptEnabled;
  }

  @Override
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    stateActions.showDriverOrderConfirmationPending(false);
    stateActions.enableAcceptButton(acceptEnabled);
    stateActions.enableDeclineButton(true);
    stateActions.showAcceptedMessage(null);
    stateActions.showDeclinedMessage(null);
    stateActions.showFailedMessage(null);
    stateActions.showTimeout(orderConfirmationTimeoutItem.getTimeout());
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

    if (acceptEnabled != that.acceptEnabled) {
      return false;
    }
    return orderConfirmationTimeoutItem.equals(that.orderConfirmationTimeoutItem);
  }

  @Override
  public int hashCode() {
    int result = orderConfirmationTimeoutItem.hashCode();
    result = 31 * result + (acceptEnabled ? 1 : 0);
    return result;
  }
}
