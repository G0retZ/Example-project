package com.fasten.executor_driver.presentation.orderroute;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.RoutePoint;

/**
 * Модель для отображения информации о точке маршрута заказа. Тестируемое форматирование.
 */
class RoutePointItem {

  @NonNull
  private final RoutePoint routePoint;

  RoutePointItem(@NonNull RoutePoint routePoint) {
    this.routePoint = routePoint;
  }

  public boolean isChecked() {
    return routePoint.isChecked();
  }

  @NonNull
  public String getAddress() {
    return routePoint.getAddress();
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
