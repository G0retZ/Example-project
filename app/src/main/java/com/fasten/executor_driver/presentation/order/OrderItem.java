package com.fasten.executor_driver.presentation.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.entity.RoutePoint;
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
    RoutePoint routePoint = getFirstOpenRoutePoint();
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
    RoutePoint routePoint = getFirstOpenRoutePoint();
    return routePoint == null ? "" : routePoint.getLatitude() + "," + routePoint.getLongitude();
  }

  public int getSecondsToMeetClient() {
    return Math.round((order.getConfirmationTime() + order.getEtaToStartPoint() * 1000
        - timeUtils.currentTimeMillis()) / 1000f);
  }

  public String getDistance() {
    return String.format(Locale.getDefault(), "%.2f", order.getDistance() / 1000d);
  }

  @NonNull
  public String getAddress() {
    RoutePoint routePoint = getFirstOpenRoutePoint();
    return routePoint == null ? ""
        : (routePoint.getAddress() + "\n" + routePoint.getComment()).trim();
  }

  public String getEstimatedPrice() {
    return order.getEstimatedPrice().trim();
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
    res[1] = order.getTimeout() * 1000 - res[1];
    res[0] = res[1] / (10L * order.getTimeout());
    return res;
  }

  @Nullable
  private RoutePoint getFirstOpenRoutePoint() {
    for (RoutePoint routePoint : order.getRoutePath()) {
      if (!routePoint.isChecked()) {
        return routePoint;
      }
    }
    return null;
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
