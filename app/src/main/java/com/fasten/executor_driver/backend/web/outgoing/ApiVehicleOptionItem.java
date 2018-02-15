package com.fasten.executor_driver.backend.web.outgoing;

import android.support.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Объект опции ТС для сериализации в JSON для отправки в АПИ
 */
public class ApiVehicleOptionItem {

  @SerializedName("id")
  @Expose
  private final int id;
  @NonNull
  @SerializedName("value")
  @Expose
  private final String value;

  public ApiVehicleOptionItem(int id, @NonNull String value) {
    this.id = id;
    this.value = value;
  }

  public int getId() {
    return id;
  }

  @NonNull
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "ApiVehicleOptionItem{" +
        "id=" + id +
        ", value='" + value + '\'' +
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

    ApiVehicleOptionItem that = (ApiVehicleOptionItem) o;

    return id == that.id && value.equals(that.value);
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + value.hashCode();
    return result;
  }
}
