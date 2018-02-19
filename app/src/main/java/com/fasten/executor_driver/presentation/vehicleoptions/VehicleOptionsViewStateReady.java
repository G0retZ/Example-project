package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние гоновности списка опций ТС.
 */
final class VehicleOptionsViewStateReady implements ViewState<VehicleOptionsViewActions> {

  @NonNull
  private final List<VehicleOptionsListItem<?>> chooseVehicleListItems;

  VehicleOptionsViewStateReady(@NonNull List<VehicleOptionsListItem<?>> chooseVehicleListItems) {
    this.chooseVehicleListItems = chooseVehicleListItems;
  }

  @Override
  public void apply(@NonNull VehicleOptionsViewActions stateActions) {
    stateActions.enableReadyButton(true);
    stateActions.showVehicleOptionsList(true);
    stateActions.showVehicleOptionsPending(false);
    stateActions.showVehicleOptionsListErrorMessage(false);
    stateActions.setVehicleOptionsListItems(chooseVehicleListItems);
  }

  @Override
  public String toString() {
    return "VehicleOptionsViewStateInitial{" +
        "chooseVehicleListItems=" + chooseVehicleListItems +
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

    return chooseVehicleListItems.equals(that.chooseVehicleListItems);
  }

  @Override
  public int hashCode() {
    return chooseVehicleListItems.hashCode();
  }
}
