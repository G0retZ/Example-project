package com.fasten.executor_driver.presentation.orderconfirmation;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Общее состояние вида заказа.
 */
class OrderConfirmationViewState implements ViewState<OrderConfirmationViewActions> {

  @Nullable
  private final OrderConfirmationItem orderConfirmationItem;

  OrderConfirmationViewState(@Nullable OrderConfirmationItem orderConfirmationItem) {
    this.orderConfirmationItem = orderConfirmationItem;
  }

  @Override
  @CallSuper
  public void apply(@NonNull OrderConfirmationViewActions stateActions) {
    if (orderConfirmationItem == null) {
      return;
    }
    stateActions.showLoadPoint(orderConfirmationItem.getLoadPointMapUrl());
    stateActions.showDistance(orderConfirmationItem.getDistance());
    stateActions.showLoadPointAddress(orderConfirmationItem.getAddress());
    stateActions.showOrderOptionsRequirements(orderConfirmationItem.getOrderOptionsRequired());
    stateActions.showEstimatedPrice(orderConfirmationItem.getEstimatedPrice());
    stateActions.showOrderConfirmationComment(orderConfirmationItem.getOfferComment());
  }

  @Override
  public String toString() {
    return "OrderConfirmationViewState{" +
        "orderConfirmationItem=" + orderConfirmationItem +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderConfirmationViewState that = (OrderConfirmationViewState) o;

    return orderConfirmationItem != null ? orderConfirmationItem.equals(that.orderConfirmationItem)
        : that.orderConfirmationItem
            == null;
  }

  @Override
  public int hashCode() {
    return orderConfirmationItem != null ? orderConfirmationItem.hashCode() : 0;
  }
}
