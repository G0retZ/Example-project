package com.cargopull.executor_driver.presentation.ordercost;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Общее состояние вида текущей стоимости заказа.
 */
class OrderCostViewState implements ViewState<OrderCostViewActions> {

  private final long orderCost;

  OrderCostViewState(long orderCost) {
    this.orderCost = orderCost;
  }

  @Override
  public void apply(@NonNull OrderCostViewActions stateActions) {
    stateActions.setOrderCostText(orderCost);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderCostViewState that = (OrderCostViewState) o;

    return orderCost == that.orderCost;
  }

  @Override
  public int hashCode() {
    return (int) (orderCost ^ (orderCost >>> 32));
  }
}
