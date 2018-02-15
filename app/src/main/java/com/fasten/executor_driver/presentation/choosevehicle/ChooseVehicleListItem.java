package com.fasten.executor_driver.presentation.choosevehicle;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.Vehicle;

/**
 * Модель для отображения ТС в списке ТС исполнителя. Тестируемое форматирование.
 */
class ChooseVehicleListItem {

  @NonNull
  private final Vehicle vehicle;

  ChooseVehicleListItem(@NonNull Vehicle vehicle) {
    this.vehicle = vehicle;
  }

  @NonNull
  String getName() {
    return vehicle.getManufacturer() + " " + vehicle.getModel()
        + " (" + vehicle.getLicensePlate() + ")";
  }

  boolean isSelectable() {
    return !vehicle.isBusy();
  }

  @StringRes
  int getLabel() {
    return vehicle.isBusy() ? R.string.busy : R.string.free;
  }

  @Override
  public String toString() {
    return "ChooseVehicleListItem{" +
        "vehicle=" + vehicle +
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

    ChooseVehicleListItem that = (ChooseVehicleListItem) o;

    return vehicle.equals(that.vehicle);
  }

  @Override
  public int hashCode() {
    return vehicle.hashCode();
  }
}
