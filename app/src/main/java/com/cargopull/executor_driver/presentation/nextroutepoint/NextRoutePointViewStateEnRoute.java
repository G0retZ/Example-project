package com.cargopull.executor_driver.presentation.nextroutepoint;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида движения к следующей маршрутной точки заказа.
 */
final class NextRoutePointViewStateEnRoute implements ViewState<NextRoutePointViewActions> {

  @NonNull
  private final RoutePointItem routePointItem;

  NextRoutePointViewStateEnRoute(@NonNull RoutePointItem routePointItem) {
    this.routePointItem = routePointItem;
  }

  @Override
  public void apply(@NonNull NextRoutePointViewActions stateActions) {
    stateActions.showNextRoutePoint(routePointItem.getMapUrl());
    stateActions.showNextRoutePointAddress(routePointItem.getCoordinatesString(),
        routePointItem.getAddress());
    stateActions.showNextRoutePointComment(routePointItem.getComment());
    stateActions.showNextRoutePointPending(false);
    stateActions.showCloseNextRoutePointAction(true);
    stateActions.showCompleteOrderAction(false);
    stateActions.showNoRouteRide(false);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NextRoutePointViewStateEnRoute that = (NextRoutePointViewStateEnRoute) o;

    return routePointItem.equals(that.routePointItem);
  }

  @Override
  public int hashCode() {
    return routePointItem.hashCode();
  }
}
