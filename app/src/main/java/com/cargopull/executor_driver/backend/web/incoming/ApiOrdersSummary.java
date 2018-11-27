package com.cargopull.executor_driver.backend.web.incoming;

import com.google.gson.annotations.SerializedName;

/**
 * ответ от API содержащий данные сводки истории заказов.
 */
public class ApiOrdersSummary {

  @SerializedName("count")
  private long count;
  @SerializedName("totalAmount")
  private long totalAmount;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrdersSummary() {
  }

  public ApiOrdersSummary(long count, long totalAmount) {
    this.count = count;
    this.totalAmount = totalAmount;
  }

  long getCount() {
    return count;
  }

  public long getTotalAmount() {
    return totalAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ApiOrdersSummary that = (ApiOrdersSummary) o;

    if (count != that.count) {
      return false;
    }
    return totalAmount == that.totalAmount;
  }

  @Override
  public int hashCode() {
    int result = (int) (count ^ (count >>> 32));
    result = 31 * result + (int) (totalAmount ^ (totalAmount >>> 32));
    return result;
  }
}
