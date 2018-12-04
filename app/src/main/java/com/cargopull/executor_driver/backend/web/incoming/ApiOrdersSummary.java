package com.cargopull.executor_driver.backend.web.incoming;

import com.google.gson.annotations.SerializedName;

/**
 * ответ от API содержащий данные сводки истории заказов.
 */
public class ApiOrdersSummary {

  @SerializedName("count")
  private int count;
  @SerializedName("totalAmount")
  private long totalAmount;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrdersSummary() {
  }

  public ApiOrdersSummary(int count, long totalAmount) {
    this.count = count;
    this.totalAmount = totalAmount;
  }

  public int getCount() {
    return count;
  }

  public long getTotalAmount() {
    return totalAmount;
  }
}
