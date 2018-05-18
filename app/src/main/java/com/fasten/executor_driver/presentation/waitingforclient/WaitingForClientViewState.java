package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Общее состояние вида ожидания клиента.
 */
class WaitingForClientViewState implements ViewState<WaitingForClientViewActions> {

  @Nullable
  private final OrderItem orderItem;

  WaitingForClientViewState(@Nullable OrderItem orderItem) {
    this.orderItem = orderItem;
  }

  @Override
  @CallSuper
  public void apply(@NonNull WaitingForClientViewActions stateActions) {
    if (orderItem == null) {
      return;
    }
    stateActions.showComment(orderItem.getOrderComment());
    stateActions.showEstimatedPrice(orderItem.getEstimatedPrice());
    stateActions.showOrderOptionsRequirements(orderItem.getOrderOptionsRequired());
  }

  @Override
  public String toString() {
    return "WaitingForClientViewState{" +
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

    WaitingForClientViewState that = (WaitingForClientViewState) o;

    return orderItem != null ? orderItem.equals(that.orderItem) : that.orderItem == null;
  }

  @Override
  public int hashCode() {
    return orderItem != null ? orderItem.hashCode() : 0;
  }
}
