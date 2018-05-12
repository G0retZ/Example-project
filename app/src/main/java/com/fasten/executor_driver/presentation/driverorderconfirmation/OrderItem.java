package com.fasten.executor_driver.presentation.driverorderconfirmation;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.Order;
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

  OrderItem(@NonNull Order order, TimeUtils timeUtils) {
    this.order = order;
    this.timeUtils = timeUtils;
    timestamp = timeUtils.currentTimeMillis();
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
        + "&size=360x304"
        + "&maptype=roadmap"
        + "&key=" + BuildConfig.STATIC_MAP_KEY;
  }

  public String getDistance() {
    return String.format(Locale.getDefault(), "%.2f", order.getDistance() / 1000d);
  }

  @NonNull
  public String getAddress() {
    return order.getRoutePoint().getAddress();
  }

  public String getEstimatedPrice() {
    return order.getEstimatedPrice();
  }

  public String getOfferOptionsRequired() {
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
  public String getOfferComment() {
    return order.getComment();
  }

  @NonNull
  public String getPrice() {
    return order.getEstimatedPrice();
  }

  public long[] getProgressLeft() {
    long[] res = new long[2];
    res[1] = timeUtils.currentTimeMillis() - timestamp;
    res[1] = order.getTimeout() * 1000 - res[1];
    res[0] = res[1] / (10L * order.getTimeout());
    return res;
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

    OrderItem orderItem = (OrderItem) o;

    return order.equals(orderItem.order);
  }

  @Override
  public int hashCode() {
    return order.hashCode();
  }
}
