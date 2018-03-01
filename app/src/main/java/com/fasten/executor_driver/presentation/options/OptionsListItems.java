package com.fasten.executor_driver.presentation.options;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Модель для отображения осписка опций ТС и исполнителя. Контейнер для моделей опций.
 */
public class OptionsListItems {

  @NonNull
  private final List<OptionsListItem<?>> vehicleOptions;
  @NonNull
  private final List<OptionsListItem<?>> driverOptions;

  public OptionsListItems(
      @NonNull List<OptionsListItem<?>> vehicleOptions,
      @NonNull List<OptionsListItem<?>> driverOptions) {
    this.vehicleOptions = vehicleOptions;
    this.driverOptions = driverOptions;
  }

  @NonNull
  public List<OptionsListItem<?>> getVehicleOptions() {
    return vehicleOptions;
  }

  @NonNull
  public List<OptionsListItem<?>> getDriverOptions() {
    return driverOptions;
  }

  public OptionsListItem<?> get(int index) {
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
    return "OptionsListItems{" +
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

    OptionsListItems that = (OptionsListItems) o;

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
