package com.fasten.executor_driver.backend.web.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

/**
 * ответ от API содержащий данные о типе параметра ТС.
 */
class ApiVehicleOption {

  @Nullable
  @SerializedName("name")
  private String name;
  @SerializedName("dynamic")
  private boolean dynamic;
  @SerializedName("numeric")
  private boolean numeric;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiVehicleOption() {
  }

  ApiVehicleOption(@Nullable String name, boolean dynamic, boolean numeric) {
    this.name = name;
    this.dynamic = dynamic;
    this.numeric = numeric;
  }

  @Nullable
  String getName() {
    return name;
  }

  boolean isDynamic() {
    return dynamic;
  }

  boolean isNumeric() {
    return numeric;
  }

  @Override
  public String toString() {
    return "ApiVehicleOption{" +
        "name='" + name + '\'' +
        ", dynamic=" + dynamic +
        ", numeric=" + numeric +
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

    ApiVehicleOption that = (ApiVehicleOption) o;

    if (dynamic != that.dynamic) {
      return false;
    }
    if (numeric != that.numeric) {
      return false;
    }
    return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (dynamic ? 1 : 0);
    result = 31 * result + (numeric ? 1 : 0);
    return result;
  }
}
