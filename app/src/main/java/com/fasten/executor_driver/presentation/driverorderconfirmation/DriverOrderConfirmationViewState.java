package com.fasten.executor_driver.presentation.driverorderconfirmation;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Общее состояние вида заказа.
 */
class DriverOrderConfirmationViewState implements ViewState<DriverOrderConfirmationViewActions> {

  @Nullable
  private final OrderItem orderItem;

  DriverOrderConfirmationViewState(@Nullable OrderItem orderItem) {
    this.orderItem = orderItem;
  }

  @Override
  @CallSuper
  public void apply(@NonNull DriverOrderConfirmationViewActions stateActions) {
    if (orderItem == null) {
      return;
    }
    stateActions.showLoadPoint(orderItem.getLoadPointMapUrl());
    stateActions.showDistance(orderItem.getDistance());
    stateActions.showLoadPointAddress(orderItem.getAddress());
    stateActions.showOfferOptionsRequirements(orderItem.getOfferOptionsRequired());
    stateActions.showEstimatedPrice(orderItem.getEstimatedPrice());
    stateActions.showComment(orderItem.getOfferComment());
    long timeout[] = orderItem.getProgressLeft();
    stateActions.showTimeout((int) timeout[0], timeout[1]);
  }

  @Override
  public String toString() {
    return "DriverOrderConfirmationViewState{" +
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

    DriverOrderConfirmationViewState that = (DriverOrderConfirmationViewState) o;

    return orderItem != null ? orderItem.equals(that.orderItem) : that.orderItem == null;
  }

  @Override
  public int hashCode() {
    return orderItem != null ? orderItem.hashCode() : 0;
  }
}
