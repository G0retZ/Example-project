package com.fasten.executor_driver.presentation.ordertime;

import android.support.annotation.NonNull;

/**
 * Состояние вида ошибки текущего времени заказа.
 */
final class OrderTimeViewStateError extends OrderTimeViewState {

  OrderTimeViewStateError(long orderCost) {
    super(orderCost);
  }

  @Override
  public void apply(@NonNull OrderTimeViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderTimeNetworkErrorMessage(true);
  }
}
