package com.fasten.executor_driver.presentation.ordercost;

import android.support.annotation.NonNull;

/**
 * Состояние вида текущей стоимости заказа.
 */
final class OrderCostViewStateIdle extends OrderCostViewState {

  OrderCostViewStateIdle(int orderCost) {
    super(orderCost);
  }

  @Override
  public void apply(@NonNull OrderCostViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderCostNetworkErrorMessage(false);
  }
}
