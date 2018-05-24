package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.entity.RoutePoint;
import java.util.List;

/**
 * Модель для отображения информации о следующей точке заказа. Тестируемое форматирование.
 */
class RoutePointItem {

  @NonNull
  private final RoutePoint routePoint;

  RoutePointItem(@NonNull List<RoutePoint> routePoints) {
    for (RoutePoint routePoint1 : routePoints) {
      if (!routePoint1.isChecked()) {
        routePoint = routePoint1;
        return;
      }
    }
    routePoint = routePoints.get(routePoints.size() - 1);
  }

  @SuppressWarnings("SpellCheckingInspection")
  @NonNull
  public String getMapUrl() {
    return "https://maps.googleapis.com/maps/api/staticmap?"
        + "key=" + BuildConfig.STATIC_MAP_KEY
        + "&center=" + routePoint.getLatitude() + "," + routePoint.getLongitude()
        + "&maptype=roadmap"
        + "&zoom=16"
        + "&size=360x200";
  }

  @NonNull
  public String getCoordinatesString() {
    return routePoint.getLatitude() + "," + routePoint.getLongitude();
  }

  @NonNull
  public String getAddress() {
    return routePoint.getAddress();
  }

  @NonNull
  public String getComment() {
    return routePoint.getComment();
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
