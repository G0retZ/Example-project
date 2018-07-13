package com.cargopull.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида движения без маршрута.
 */
final class NextRoutePointViewStateNoRoute implements ViewState<NextRoutePointViewActions> {

  private final boolean noRouteRide;

  NextRoutePointViewStateNoRoute(boolean noRouteRide) {
    this.noRouteRide = noRouteRide;
  }

  @Override
  public void apply(@NonNull NextRoutePointViewActions stateActions) {
    stateActions.showNextRoutePoint("");
    stateActions.showNextRoutePointAddress("", "");
    stateActions.showNextRoutePointComment("");
    stateActions.showNextRoutePointPending(false);
    stateActions.showCloseNextRoutePointAction(false);
    stateActions.showCompleteOrderAction(true);
    stateActions.showNoRouteRide(noRouteRide);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NextRoutePointViewStateNoRoute that = (NextRoutePointViewStateNoRoute) o;

    return noRouteRide == that.noRouteRide;
  }

  @Override
  public int hashCode() {
    return (noRouteRide ? 1 : 0);
  }
}
