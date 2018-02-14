package com.fasten.executor_driver.backend.web.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

/**
 * ответ от API содержащий данные о параметре ТС.
 */
public class ApiVehicleOptionItem {

  @SerializedName("id")
  private long id;
  @Nullable
  @SerializedName("value")
  private String value;
  @Nullable
  @SerializedName("dictionary")
  private ApiVehicleOptionItemLimits limits;
  @Nullable
  @SerializedName("vehicleOption")
  private ApiVehicleOption option;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiVehicleOptionItem() {
  }

  public ApiVehicleOptionItem(long id, @Nullable String value,
      @Nullable ApiVehicleOptionItemLimits limits,
      @Nullable ApiVehicleOption option) {
    this.id = id;
    this.value = value;
    this.limits = limits;
    this.option = option;
  }

  public long getId() {
    return id;
  }

  @Nullable
  public String getValue() {
    return value;
  }

  @Nullable
  public ApiVehicleOptionItemLimits getLimits() {
    return limits;
  }

  @Nullable
  public ApiVehicleOption getOption() {
    return option;
  }

  @Override
  public String toString() {
    return "ApiVehicleOptionItem{" +
        "id=" + id +
        ", value='" + value + '\'' +
        ", limits=" + limits +
        ", option=" + option +
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

    ApiVehicleOptionItem that = (ApiVehicleOptionItem) o;

    if (id != that.id) {
      return false;
    }
    if (value != null ? !value.equals(that.value) : that.value != null) {
      return false;
    }
    if (limits != null ? !limits.equals(that.limits) : that.limits != null) {
      return false;
    }
    return option != null ? option.equals(that.option) : that.option == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (value != null ? value.hashCode() : 0);
    result = 31 * result + (limits != null ? limits.hashCode() : 0);
    result = 31 * result + (option != null ? option.hashCode() : 0);
    return result;
  }
}
