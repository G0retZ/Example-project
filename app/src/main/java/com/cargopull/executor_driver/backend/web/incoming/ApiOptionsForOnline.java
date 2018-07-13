package com.cargopull.executor_driver.backend.web.incoming;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * ответ от API содержащий список ТС и список опций водителя.
 */
public class ApiOptionsForOnline {

  @SerializedName("cars")
  private List<ApiVehicle> cars;
  @SerializedName("driverOptions")
  private List<ApiOptionItem> driverOptions;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOptionsForOnline() {
  }

  public ApiOptionsForOnline(List<ApiVehicle> cars, List<ApiOptionItem> driverOptions) {
    this.cars = cars;
    this.driverOptions = driverOptions;
  }

  public List<ApiVehicle> getCars() {
    return cars;
  }

  public List<ApiOptionItem> getDriverOptions() {
    return driverOptions;
  }

  @Override
  public String toString() {
    return "ApiOptionsForOnline{" +
        "cars=" + cars +
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

    ApiOptionsForOnline that = (ApiOptionsForOnline) o;

    if (cars != null ? !cars.equals(that.cars) : that.cars != null) {
      return false;
    }
    return driverOptions != null ? driverOptions.equals(that.driverOptions)
        : that.driverOptions == null;
  }

  @Override
  public int hashCode() {
    int result = cars != null ? cars.hashCode() : 0;
    result = 31 * result + (driverOptions != null ? driverOptions.hashCode() : 0);
    return result;
  }
}
