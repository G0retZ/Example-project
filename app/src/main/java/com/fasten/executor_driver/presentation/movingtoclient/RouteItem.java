package com.fasten.executor_driver.presentation.movingtoclient;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.utils.TimeUtils;

/**
 * Модель для отображения информации о первой точке заказа. Тестируемое форматирование.
 */
class RouteItem {

  @NonNull
  private final Order order;
  @NonNull
  private final TimeUtils timeUtils;

  RouteItem(@NonNull Order order, @NonNull TimeUtils timeUtils) {
    this.order = order;
    this.timeUtils = timeUtils;
  }

  @SuppressWarnings("SpellCheckingInspection")
  @NonNull
  public String getLoadPointMapUrl() {
    return "https://maps.googleapis.com/maps/api/staticmap?"
        + "center="
        + order.getRoutePoint().getLatitude()
        + ","
        + order.getRoutePoint().getLongitude()
        + "&zoom=16"
        + "&size=360x200"
        + "&maptype=roadmap"
        + "&key=" + BuildConfig.STATIC_MAP_KEY;
  }

  @NonNull
  public String getCoordinatesString() {
    return order.getRoutePoint().getLatitude() + "," + order.getRoutePoint().getLongitude();
  }

  public int getSecondsToMeetClient() {
    return Math.round((order.getConfirmationTime() + order.getEtaToStartPoint() * 1000
        - timeUtils.currentTimeMillis()) / 1000f);
  }

  @NonNull
  public String getAddress() {
    return (order.getRoutePoint().getAddress() + "\n" + order.getRoutePoint().getComment()).trim();
  }

  @Override
  public String toString() {
    return "RouteItem{" +
        "order=" + order +
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

    RouteItem that = (RouteItem) o;

    return order.equals(that.order);
  }

  @Override
  public int hashCode() {
    return order.hashCode();
  }
}
