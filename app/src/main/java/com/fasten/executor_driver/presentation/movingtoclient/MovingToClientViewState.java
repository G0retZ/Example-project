package com.fasten.executor_driver.presentation.movingtoclient;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Общее состояние вида заказа.
 */
class MovingToClientViewState implements ViewState<MovingToClientViewActions> {

  @Nullable
  private final RouteItem routeItem;

  MovingToClientViewState(@Nullable RouteItem routeItem) {
    this.routeItem = routeItem;
  }

  @Override
  @CallSuper
  public void apply(@NonNull MovingToClientViewActions stateActions) {
    if (routeItem == null) {
      return;
    }
    stateActions.showLoadPoint(routeItem.getLoadPointMapUrl());
    stateActions.showLoadPointCoordinates(routeItem.getCoordinatesString());
    stateActions.showLoadPointAddress(routeItem.getAddress());
    stateActions.showTimeout(routeItem.getSecondsToMeetClient());
  }

  @Override
  public String toString() {
    return "MovingToClientViewState{" +
        "routeItem=" + routeItem +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MovingToClientViewState that = (MovingToClientViewState) o;

    return routeItem != null ? routeItem.equals(that.routeItem) : that.routeItem == null;
  }

  @Override
  public int hashCode() {
    return routeItem != null ? routeItem.hashCode() : 0;
  }
}
