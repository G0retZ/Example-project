package com.fasten.executor_driver.presentation.clientorderconfirmation;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.Order;
import java.util.Locale;

/**
 * Модель для отображения предложения заказа. Тестируемое форматирование.
 */
class OrderItem {

  @NonNull
  private final Order order;

  OrderItem(@NonNull Order order) {
    this.order = order;
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

  public String getDistance() {
    return String.format(Locale.getDefault(), "%.2f", order.getDistance() / 1000d);
  }

  @NonNull
  public String getAddress() {
    return order.getRoutePoint().getAddress().trim() + "\n" + order.getRoutePoint().getComment()
        .trim();
  }

  public String getEstimatedPrice() {
    return order.getEstimatedPrice();
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
  public String getPrice() {
    return order.getEstimatedPrice().trim();
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
