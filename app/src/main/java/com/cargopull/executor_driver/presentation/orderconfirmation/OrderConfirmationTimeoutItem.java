package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.utils.TimeUtils;

/**
 * Модель для отображения таймаута предложения заказа. Тестируемое форматирование.
 */
class OrderConfirmationTimeoutItem {

  private final long timeout;
  @NonNull
  private final TimeUtils timeUtils;
  private final long timestamp;

  OrderConfirmationTimeoutItem(long timeout, @NonNull TimeUtils timeUtils) {
    this.timeout = timeout;
    this.timeUtils = timeUtils;
    timestamp = timeUtils.currentTimeMillis();
  }

  long getItemTimestamp() {
    return timestamp;
  }

  public long getTimeout() {
    return timeout - timeUtils.currentTimeMillis() + timestamp;
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderConfirmationTimeoutItem that = (OrderConfirmationTimeoutItem) o;

    if (timeout != that.timeout) {
      return false;
    }
    if (timestamp != that.timestamp) {
      return false;
    }
    return timeUtils.equals(that.timeUtils);
  }

  @Override
  public int hashCode() {
    int result = (int) (timeout ^ (timeout >>> 32));
    result = 31 * result + timeUtils.hashCode();
    result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
    return result;
  }
}
