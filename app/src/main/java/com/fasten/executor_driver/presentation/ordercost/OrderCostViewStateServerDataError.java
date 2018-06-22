package com.fasten.executor_driver.presentation.ordercost;

import android.support.annotation.NonNull;

/**
 * Состояние вида ошибки текущей стоимости заказа.
 */
final class OrderCostViewStateServerDataError extends OrderCostViewState {

  OrderCostViewStateServerDataError(int orderCost) {
    super(orderCost);
  }

  @Override
  public void apply(@NonNull OrderCostViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderCostServerDataError();
  }
}
