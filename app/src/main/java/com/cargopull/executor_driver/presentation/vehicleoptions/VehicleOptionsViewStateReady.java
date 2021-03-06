package com.cargopull.executor_driver.presentation.vehicleoptions;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние гоновности списка опций ТС.
 */
public final class VehicleOptionsViewStateReady implements ViewState<VehicleOptionsViewActions> {

  @NonNull
  private final VehicleOptionsListItems vehicleOptionsListItems;

  VehicleOptionsViewStateReady(@NonNull VehicleOptionsListItems vehicleOptionsListItems) {
    this.vehicleOptionsListItems = vehicleOptionsListItems;
  }

  @Override
  public void apply(@NonNull VehicleOptionsViewActions stateActions) {
    stateActions.enableReadyButton(true);
    stateActions.showVehicleOptionsList(true);
    stateActions.showVehicleOptionsPending(false);
    stateActions.showVehicleOptionsListErrorMessage(false);
    stateActions.setVehicleOptionsListItems(vehicleOptionsListItems);
    // Убираем диалог
    stateActions.dismissDialog();
  }

  @Override
  public String toString() {
    return "VehicleOptionsViewStateInitial{" +
        "vehicleOptionsListItems=" + vehicleOptionsListItems +
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

    VehicleOptionsViewStateReady that = (VehicleOptionsViewStateReady) o;

    return vehicleOptionsListItems.equals(that.vehicleOptionsListItems);
  }

  @Override
  public int hashCode() {
    return vehicleOptionsListItems.hashCode();
  }
}
