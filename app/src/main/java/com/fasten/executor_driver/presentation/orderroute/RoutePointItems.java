package com.fasten.executor_driver.presentation.orderroute;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Модель для отображения списка точек маршрута заказа. Контейнер для моделей точек маршрута.
 */
class RoutePointItems {

  @NonNull
  private final List<RoutePointItem> routePointItems;

  RoutePointItems(@NonNull List<RoutePointItem> routePointItems) {
    this.routePointItems = routePointItems;
  }

  public RoutePointItem get(int index) {
    return routePointItems.get(index);
  }

  public int size() {
    return routePointItems.size();
  }

  @SuppressWarnings("SimplifiableIfStatement")
  public boolean isInProgress(RoutePointItem routePointItem) {
    int index = routePointItems.indexOf(routePointItem);
    if (routePointItem.isChecked()) {
      return false;
    }
    return index == 0 || index > 0 && routePointItems.get(index - 1).isChecked();
  }

  @Override
  public String toString() {
    return "RoutePointItems{" +
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

    RoutePointItems that = (RoutePointItems) o;

    return routePointItems.equals(that.routePointItems);
  }

  @Override
  public int hashCode() {
    return routePointItems.hashCode();
  }
}
