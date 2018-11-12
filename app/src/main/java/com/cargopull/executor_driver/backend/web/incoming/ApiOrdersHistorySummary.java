package com.cargopull.executor_driver.backend.web.incoming;

import com.google.gson.annotations.SerializedName;

/**
 * ответ от API содержащий данные сводки истории заказов.
 */
public class ApiOrdersHistorySummary {

  @SerializedName("successOrders")
  private long successOrders;
  @SerializedName("refusedOrders")
  private long refusedOrders;
  @SerializedName("skippedOrders")
  private long skippedOrders;
  @SerializedName("cancelledOrders")
  private long cancelledOrders;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrdersHistorySummary() {
  }

  public ApiOrdersHistorySummary(long successOrders, long refusedOrders, long skippedOrders,
      long cancelledOrders) {
    this.successOrders = successOrders;
    this.refusedOrders = refusedOrders;
    this.skippedOrders = skippedOrders;
    this.cancelledOrders = cancelledOrders;
  }

  public long getSuccessOrders() {
    return successOrders;
  }

  public long getRefusedOrders() {
    return refusedOrders;
  }

  public long getSkippedOrders() {
    return skippedOrders;
  }

  public long getCancelledOrders() {
    return cancelledOrders;
  }

  @Override
  public String toString() {
    return "ApiOrdersHistorySummary{" +
        "successOrders=" + successOrders +
        ", refusedOrders=" + refusedOrders +
        ", skippedOrders=" + skippedOrders +
        ", cancelledOrders=" + cancelledOrders +
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

    ApiOrdersHistorySummary that = (ApiOrdersHistorySummary) o;

    if (successOrders != that.successOrders) {
      return false;
    }
    if (refusedOrders != that.refusedOrders) {
      return false;
    }
    if (skippedOrders != that.skippedOrders) {
      return false;
    }
    return cancelledOrders == that.cancelledOrders;
  }

  @Override
  public int hashCode() {
    int result = (int) (successOrders ^ (successOrders >>> 32));
    result = 31 * result + (int) (refusedOrders ^ (refusedOrders >>> 32));
    result = 31 * result + (int) (skippedOrders ^ (skippedOrders >>> 32));
    result = 31 * result + (int) (cancelledOrders ^ (cancelledOrders >>> 32));
    return result;
  }
}
