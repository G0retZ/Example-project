package com.fasten.executor_driver.presentation.orderroute;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.entity.RoutePointState;

/**
 * Модель для отображения информации о точке маршрута заказа. Тестируемое форматирование.
 */
public class RoutePointItem {

  @NonNull
  private final RoutePoint routePoint;

  RoutePointItem(@NonNull RoutePoint routePoint) {
    this.routePoint = routePoint;
  }

  public boolean isActive() {
    return routePoint.getRoutePointState() == RoutePointState.ACTIVE;
  }

  public boolean isProcessed() {
    return routePoint.getRoutePointState() == RoutePointState.PROCESSED;
  }

  @NonNull
  public String getAddress() {
    return routePoint.getAddress();
  }

  @NonNull
  public RoutePoint getRoutePoint() {
    return routePoint;
  }

  @Override
  public String toString() {
    return "RoutePointItem{" +
        "routePoint=" + routePoint +
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

    RoutePointItem that = (RoutePointItem) o;

    return routePoint.equals(that.routePoint);
  }

  @Override
  public int hashCode() {
    return routePoint.hashCode();
  }
}
