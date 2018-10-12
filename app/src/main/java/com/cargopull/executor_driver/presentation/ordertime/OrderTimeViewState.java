package com.cargopull.executor_driver.presentation.ordertime;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Общее состояние вида текущего времени заказа.
 */
class OrderTimeViewState implements ViewState<OrderTimeViewActions> {

  private final long orderTimeElapsed;

  OrderTimeViewState(long orderTimeElapsed) {
    this.orderTimeElapsed = orderTimeElapsed;
  }

  @Override
  public void apply(@NonNull OrderTimeViewActions stateActions) {
    stateActions.setOrderTimeText(orderTimeElapsed);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderTimeViewState that = (OrderTimeViewState) o;

    return orderTimeElapsed == that.orderTimeElapsed;
  }

  @Override
  public int hashCode() {
    return (int) (orderTimeElapsed ^ (orderTimeElapsed >>> 32));
  }
}
