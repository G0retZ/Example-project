package com.fasten.executor_driver.backend.web.outgoing;

import android.support.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Объект опции ТС для сериализации в JSON для отправки в АПИ.
 */
public class ApiOptionItems {

  @NonNull
  @SerializedName("vehicleOptions")
  @Expose
  private final List<ApiOptionItem> vehicleOptions;
  @NonNull
  @SerializedName("driverOptions")
  @Expose
  private final List<ApiOptionItem> driverOptions;

  public ApiOptionItems(
      @NonNull List<ApiOptionItem> vehicleOptions,
      @NonNull List<ApiOptionItem> driverOptions) {
    this.vehicleOptions = vehicleOptions;
    this.driverOptions = driverOptions;
  }

  @NonNull
  List<ApiOptionItem> getVehicleOptions() {
    return vehicleOptions;
  }

  @NonNull
  List<ApiOptionItem> getDriverOptions() {
    return driverOptions;
  }

  @Override
  public String toString() {
    return "ApiOptionItems{" +
        "vehicleOptions=" + vehicleOptions +
        ", driverOptions=" + driverOptions +
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

    ApiOptionItems that = (ApiOptionItems) o;

    if (!vehicleOptions.equals(that.vehicleOptions)) {
      return false;
    }
    return driverOptions.equals(that.driverOptions);
  }

  @Override
  public int hashCode() {
    int result = vehicleOptions.hashCode();
    result = 31 * result + driverOptions.hashCode();
    return result;
  }
}
