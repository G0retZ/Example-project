package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;

/**
 * Состояние вида ожидания следующей маршрутной точки заказа.
 */
final class NextRoutePointViewStatePending extends NextRoutePointViewState {

  NextRoutePointViewStatePending(RoutePointItem routePointItem) {
    super(routePointItem);
  }

  @Override
  public void apply(@NonNull NextRoutePointViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showNextRoutePointPending(true);
    stateActions.showNextRoutePointNetworkErrorMessage(false);
  }
}
