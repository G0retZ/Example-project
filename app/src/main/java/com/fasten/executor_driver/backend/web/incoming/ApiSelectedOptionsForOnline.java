package com.fasten.executor_driver.backend.web.incoming;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * ответ от API содержащий список опций теущего ТС и водителя.
 */
public class ApiSelectedOptionsForOnline {

  @SerializedName("vehicleOptionItems")
  private List<ApiOptionItem> vehicleOptions;
  @SerializedName("driverOptionItems")
  private List<ApiOptionItem> driverOptions;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiSelectedOptionsForOnline() {
  }

  public ApiSelectedOptionsForOnline(List<ApiOptionItem> vehicleOptions,
      List<ApiOptionItem> driverOptions) {
    this.vehicleOptions = vehicleOptions;
    this.driverOptions = driverOptions;
  }

  public List<ApiOptionItem> getVehicleOptions() {
    return vehicleOptions;
  }

  public List<ApiOptionItem> getDriverOptions() {
    return driverOptions;
  }

  @Override
  public String toString() {
    return "ApiOptionsForOnline{" +
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

    ApiSelectedOptionsForOnline that = (ApiSelectedOptionsForOnline) o;

    if (vehicleOptions != null ? !vehicleOptions.equals(that.vehicleOptions) : that.vehicleOptions
        != null) {
      return false;
    }
    return driverOptions != null ? driverOptions.equals(that.driverOptions)
        : that.driverOptions == null;
  }

  @Override
  public int hashCode() {
    int result = vehicleOptions != null ? vehicleOptions.hashCode() : 0;
    result = 31 * result + (driverOptions != null ? driverOptions.hashCode() : 0);
    return result;
  }
}
