package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние гоновности списка опций ТС.
 */
final class VehicleOptionsViewStateReady implements ViewState<VehicleOptionsViewActions> {

  @NonNull
  private final OptionsListItems optionsListItems;

  VehicleOptionsViewStateReady(@NonNull OptionsListItems optionsListItems) {
    this.optionsListItems = optionsListItems;
  }

  @Override
  public void apply(@NonNull VehicleOptionsViewActions stateActions) {
    stateActions.enableReadyButton(true);
    stateActions.showVehicleOptionsList(true);
    stateActions.showVehicleOptionsPending(false);
    stateActions.showVehicleOptionsListErrorMessage(false);
    stateActions.setVehicleOptionsListItems(optionsListItems);
  }

  @Override
  public String toString() {
    return "VehicleOptionsViewStateInitial{" +
        "optionsListItems=" + optionsListItems +
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

    return optionsListItems.equals(that.optionsListItems);
  }

  @Override
  public int hashCode() {
    return optionsListItems.hashCode();
  }
}
