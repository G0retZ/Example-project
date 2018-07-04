package com.fasten.executor_driver.presentation.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.entity.RoutePointState;
import com.fasten.executor_driver.utils.TimeUtils;
import java.util.Locale;

/**
 * Модель для отображения предложения заказа. Тестируемое форматирование.
 */
class OrderItem {

  @NonNull
  private final Order order;
  @NonNull
  private final TimeUtils timeUtils;
  private final long timestamp;

  OrderItem(@NonNull Order order, @NonNull TimeUtils timeUtils) {
    this.order = order;
    this.timeUtils = timeUtils;
    timestamp = timeUtils.currentTimeMillis();
  }

  @SuppressWarnings("SpellCheckingInspection")
  @NonNull
  public String getLoadPointMapUrl() {
    RoutePoint routePoint = getFirstActiveRoutePoint();
    return routePoint == null ? "" : "https://maps.googleapis.com/maps/api/staticmap?"
        + "center="
        + routePoint.getLatitude()
        + ","
        + routePoint.getLongitude()
        + "&zoom=16"
        + "&size=360x200"
        + "&maptype=roadmap"
        + "&key=" + BuildConfig.STATIC_MAP_KEY;
  }

  @NonNull
  public String getCoordinatesString() {
    RoutePoint routePoint = getFirstActiveRoutePoint();
    return routePoint == null ? "" : routePoint.getLatitude() + "," + routePoint.getLongitude();
  }

  public int getSecondsToMeetClient() {
    return Math.round((order.getConfirmationTime() + order.getEtaToStartPoint()
        - timeUtils.currentTimeMillis()) / 1000f);
  }

  public String getDistance() {
    return String.format(Locale.getDefault(), "%.2f", order.getDistance() / 1000d);
  }

  public int getEtaSeconds() {
    return Math.round(order.getEtaToStartPoint() / 1000f);
  }

  @NonNull
  public String getNextAddress() {
    RoutePoint routePoint = getFirstActiveRoutePoint();
    return routePoint == null ? "" : routePoint.getAddress().trim();
  }

  @NonNull
  public String getNextAddressComment() {
    RoutePoint routePoint = getFirstActiveRoutePoint();
    return routePoint == null ? "" : routePoint.getComment().trim();
  }

  @NonNull
  public String getLastAddress() {
    RoutePoint routePoint = null;
    if (order.getRoutePath().size() > 1) {
      routePoint = order.getRoutePath().get(order.getRoutePath().size() - 1);
    }
    return routePoint == null ? "" : routePoint.getAddress().trim();
  }

  public int getRoutePointsCount() {
    return order.getRoutePath().size();
  }

  public String getRouteLength() {
    return String.format(Locale.getDefault(), "%.2f", order.getEstimatedRouteLength() / 1000d);
  }

  public int getEstimatedTimeSeconds() {
    return Math.round(order.getEstimatedTime() / 1000f);
  }

  public String getServiceName() {
    return "";
  }

  public int getEstimatedPrice() {
    return order.getEstimatedPrice();
  }

  public String getEstimatedPriceText() {
    return order.getEstimatedPriceText().trim();
  }

  public String getOrderOptionsRequired() {
    StringBuilder result = new StringBuilder();
    for (Option option : order.getOptions()) {
      if (option instanceof OptionNumeric) {
        result.append(option.getName()).append(": ").append(option.getValue());
      } else if (option instanceof OptionBoolean) {
        if (!(boolean) option.getValue()) {
          continue;
        }
        result.append(option.getName());
      }
      result.append("\n");
    }
    return result.toString().trim();
  }

  @NonNull
  public String getOrderComment() {
    return order.getComment().trim();
  }

  @NonNull
  public long[] getProgressLeft() {
    long[] res = new long[2];
    res[1] = timeUtils.currentTimeMillis() - timestamp;
    res[1] = order.getTimeout() - res[1];
    if (order.getTimeout() > 0) {
      res[0] = res[1] * 100L / (order.getTimeout());
    }
    return res;
  }

  @Nullable
  private RoutePoint getFirstActiveRoutePoint() {
    for (RoutePoint routePoint : order.getRoutePath()) {
      if (routePoint.getRoutePointState() == RoutePointState.ACTIVE) {
        return routePoint;
      }
    }
    return order.getRoutePath().get(0);
  }

  @Override
  public String toString() {
    return "OrderItem{" +
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

    OrderItem that = (OrderItem) o;

    return order.equals(that.order);
  }

  @Override
  public int hashCode() {
    return order.hashCode();
  }
}
