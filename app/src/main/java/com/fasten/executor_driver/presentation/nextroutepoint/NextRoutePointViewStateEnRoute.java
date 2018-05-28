package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

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
    stateActions.showNextRoutePointAddress(routePointItem.getAddress());
    stateActions.showNextRoutePointComment(routePointItem.getComment());
    stateActions.showNextRoutePointCoordinates(routePointItem.getCoordinatesString());
    stateActions.showNextRoutePointPending(false);
    stateActions.showNextRoutePointNetworkErrorMessage(false);
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
