package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;

/**
 * Состояние вида ошибки следующей маршрутной точки заказа.
 */
final class NextRoutePointViewStateError extends NextRoutePointViewState {

  NextRoutePointViewStateError(@NonNull RoutePointItem routePointItem) {
    super(routePointItem);
  }

  @Override
  public void apply(@NonNull NextRoutePointViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showNextRoutePointPending(false);
    stateActions.showNextRoutePointNetworkErrorMessage(true);
  }
}
