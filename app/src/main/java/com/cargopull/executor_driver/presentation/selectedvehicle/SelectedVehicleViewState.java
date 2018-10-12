package com.cargopull.executor_driver.presentation.selectedvehicle;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида информации о выбранном ТС.
 */
final class SelectedVehicleViewState implements ViewState<SelectedVehicleViewActions> {

  @NonNull
  private final String vehicleName;

  SelectedVehicleViewState(@NonNull String vehicleName) {
    this.vehicleName = vehicleName;
  }

  @Override
  public void apply(@NonNull SelectedVehicleViewActions stateActions) {
    stateActions.enableChangeButton(!vehicleName.isEmpty());
    stateActions.setVehicleName(vehicleName.isEmpty() ? "--" : vehicleName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SelectedVehicleViewState that = (SelectedVehicleViewState) o;

    return vehicleName.equals(that.vehicleName);
  }

  @Override
  public int hashCode() {
    return vehicleName.hashCode();
  }
}
