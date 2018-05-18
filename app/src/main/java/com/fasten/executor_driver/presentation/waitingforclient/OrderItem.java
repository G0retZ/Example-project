package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.Order;

/**
 * Модель для отображения заказа при ожидании клиента. Тестируемое форматирование.
 */
class OrderItem {

  @NonNull
  private final Order order;

  OrderItem(@NonNull Order order) {
    this.order = order;
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
