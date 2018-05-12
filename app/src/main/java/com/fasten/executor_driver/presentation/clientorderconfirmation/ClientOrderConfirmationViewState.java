package com.fasten.executor_driver.presentation.clientorderconfirmation;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Общее состояние вида заказа.
 */
class ClientOrderConfirmationViewState implements ViewState<ClientOrderConfirmationViewActions> {

  @Nullable
  private final OrderItem orderItem;

  ClientOrderConfirmationViewState(@Nullable OrderItem orderItem) {
    this.orderItem = orderItem;
  }

  @Override
  @CallSuper
  public void apply(@NonNull ClientOrderConfirmationViewActions stateActions) {
    if (orderItem == null) {
      return;
    }
    stateActions.showLoadPoint(orderItem.getLoadPointMapUrl());
    stateActions.showDistance(orderItem.getDistance());
    stateActions.showLoadPointAddress(orderItem.getAddress());
    stateActions.showOptionsRequirements(orderItem.getOrderOptionsRequired());
    stateActions.showEstimatedPrice(orderItem.getEstimatedPrice());
    stateActions.showComment(orderItem.getOfferComment());
  }

  @Override
  public String toString() {
    return "ClientOrderConfirmationViewState{" +
        "orderItem=" + orderItem +
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

    ClientOrderConfirmationViewState that = (ClientOrderConfirmationViewState) o;

    return orderItem != null ? orderItem.equals(that.orderItem)
        : that.orderItem
            == null;
  }

  @Override
  public int hashCode() {
    return orderItem != null ? orderItem.hashCode() : 0;
  }
}
