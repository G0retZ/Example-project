package com.fasten.executor_driver.presentation.orderroute;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние гоновности списка точек маршрута заказа.
 */
public final class OrderRouteViewState implements ViewState<OrderRouteViewActions> {

  @NonNull
  private final List<RoutePointItem> routePointItems;

  OrderRouteViewState(@NonNull List<RoutePointItem> routePointItems) {
    this.routePointItems = routePointItems;
  }

  @Override
  public void apply(@NonNull OrderRouteViewActions stateActions) {
    stateActions.showOrderRoutePending(false);
    stateActions.setRoutePointItems(routePointItems);
  }

  @Override
  public String toString() {
    return "OrderRouteViewState{" +
        "routePointItems=" + routePointItems +
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

    OrderRouteViewState that = (OrderRouteViewState) o;

    return routePointItems.equals(that.routePointItems);
  }

  @Override
  public int hashCode() {
    return routePointItems.hashCode();
  }
}
