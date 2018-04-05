package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Модель для отображения осписка опций ТС и исполнителя. Контейнер для моделей опций.
 */
public class VehicleOptionsListItems {

  @NonNull
  private final List<VehicleOptionsListItem<?>> vehicleOptions;
  @NonNull
  private final List<VehicleOptionsListItem<?>> driverOptions;

  public VehicleOptionsListItems(
      @NonNull List<VehicleOptionsListItem<?>> vehicleOptions,
      @NonNull List<VehicleOptionsListItem<?>> driverOptions) {
    this.vehicleOptions = vehicleOptions;
    this.driverOptions = driverOptions;
  }

  @NonNull
  public List<VehicleOptionsListItem<?>> getVehicleOptions() {
    return vehicleOptions;
  }

  @NonNull
  public List<VehicleOptionsListItem<?>> getDriverOptions() {
    return driverOptions;
  }

  public VehicleOptionsListItem<?> get(int index) {
    if (index < vehicleOptions.size()) {
      return vehicleOptions.get(index);
    }
    return driverOptions.get(index - vehicleOptions.size());
  }

  public int size() {
    return vehicleOptions.size() + driverOptions.size();
  }

  @Override
  public String toString() {
    return "VehicleOptionsListItems{" +
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

    VehicleOptionsListItems that = (VehicleOptionsListItems) o;

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
