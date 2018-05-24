package com.fasten.executor_driver.presentation.ordertime;

import android.support.annotation.NonNull;

/**
 * Состояние вида бездействия текущего времени заказа.
 */
final class OrderTimeViewStateIdle extends OrderTimeViewState {

  OrderTimeViewStateIdle(long orderCost) {
    super(orderCost);
  }

  @Override
  public void apply(@NonNull OrderTimeViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOrderTimeNetworkErrorMessage(false);
  }
}
