package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Общее состояние вида следующей маршрутной точки заказа.
 */
class NextRoutePointViewState implements ViewState<NextRoutePointViewActions> {

  @NonNull
  private final RoutePointItem routePointItem;

  NextRoutePointViewState(@NonNull RoutePointItem routePointItem) {
    this.routePointItem = routePointItem;
  }

  @Override
  public void apply(@NonNull NextRoutePointViewActions stateActions) {
    stateActions.showNextRoutePoint(routePointItem.getMapUrl());
    stateActions.showNextRoutePointAddress(routePointItem.getAddress());
    stateActions.showNextRoutePointComment(routePointItem.getComment());
    stateActions.showNextRoutePointCoordinates(routePointItem.getCoordinatesString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NextRoutePointViewState that = (NextRoutePointViewState) o;

    return routePointItem.equals(that.routePointItem);
  }

  @Override
  public int hashCode() {
    return routePointItem.hashCode();
  }
}
