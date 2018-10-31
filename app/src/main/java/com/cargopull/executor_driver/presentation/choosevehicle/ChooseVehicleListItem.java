package com.cargopull.executor_driver.presentation.choosevehicle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Vehicle;

/**
 * Модель для отображения ТС в списке ТС исполнителя. Тестируемое форматирование.
 */
public class ChooseVehicleListItem {

  @NonNull
  private final Vehicle vehicle;

  ChooseVehicleListItem(@NonNull Vehicle vehicle) {
    this.vehicle = vehicle;
  }

  @NonNull
  public Vehicle getVehicle() {
    return vehicle;
  }

  @NonNull
  public String getName() {
    return vehicle.getManufacturer() + " " + vehicle.getModel()
        + " (" + vehicle.getLicensePlate() + ")";
  }

  public boolean isSelectable() {
    return !vehicle.isBusy();
  }

  @StringRes
  public int getLabel() {
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
