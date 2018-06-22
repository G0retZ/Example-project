package com.fasten.executor_driver.presentation.ordertime;

import android.support.annotation.NonNull;

/**
 * Состояние вида ошибки текущего времени заказа.
 */
final class OrderTimeViewStateServerDataError extends OrderTimeViewState {

  OrderTimeViewStateServerDataError(long orderCost) {
    super(orderCost);
  }

  @Override
  public void apply(@NonNull OrderTimeViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderTimeServerDataError();
  }
}
