package com.fasten.executor_driver.presentation.vehicleoptions;

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

  OptionsListItems(
      @NonNull List<OptionsListItem<?>> vehicleOptions,
      @NonNull List<OptionsListItem<?>> driverOptions) {
    this.vehicleOptions = vehicleOptions;
    this.driverOptions = driverOptions;
  }

  @NonNull
  List<OptionsListItem<?>> getVehicleOptions() {
    return vehicleOptions;
  }

  @NonNull
  List<OptionsListItem<?>> getDriverOptions() {
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

}
