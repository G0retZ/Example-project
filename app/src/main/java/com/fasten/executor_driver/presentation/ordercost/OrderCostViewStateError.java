package com.fasten.executor_driver.presentation.ordercost;

import android.support.annotation.NonNull;

/**
 * Состояние вида текущей стоимости заказа.
 */
final class OrderCostViewStateError extends OrderCostViewState {

  OrderCostViewStateError(int orderCost) {
    super(orderCost);
  }

  @Override
  public void apply(@NonNull OrderCostViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderCostNetworkErrorMessage(true);
  }
}
