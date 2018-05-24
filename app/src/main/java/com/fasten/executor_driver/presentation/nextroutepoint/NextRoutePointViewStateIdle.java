package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;

/**
 * Состояние вида бездействия следующей маршрутной точки заказа.
 */
final class NextRoutePointViewStateIdle extends NextRoutePointViewState {

  NextRoutePointViewStateIdle(@NonNull RoutePointItem routePointItem) {
    super(routePointItem);
  }

  @Override
  public void apply(@NonNull NextRoutePointViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showNextRoutePointPending(false);
    stateActions.showNextRoutePointNetworkErrorMessage(false);
  }
}
