package com.fasten.executor_driver.backend.web.incoming;

import com.google.gson.annotations.SerializedName;

/**
 * ответ от API содержащий данные об ограничениях параметра ТС.
 */
class ApiVehicleOptionItemLimits {

  @SerializedName("minValue")
  private int minValue;
  @SerializedName("maxValue")
  private int maxValue;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiVehicleOptionItemLimits() {
  }

  ApiVehicleOptionItemLimits(int minValue, int maxValue) {
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  int getMinValue() {
    return minValue;
  }

  int getMaxValue() {
    return maxValue;
  }

  @Override
  public String toString() {
    return "ApiVehicleOptionItemLimits{" +
        "minValue=" + minValue +
        ", maxValue=" + maxValue +
        '}';
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

    ApiVehicleOptionItemLimits that = (ApiVehicleOptionItemLimits) o;

    if (minValue != that.minValue) {
      return false;
    }
    return maxValue == that.maxValue;
  }

  @Override
  public int hashCode() {
    int result = minValue;
    result = 31 * result + maxValue;
    return result;
  }
}
