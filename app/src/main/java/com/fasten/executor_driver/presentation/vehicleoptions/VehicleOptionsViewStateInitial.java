package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import java.util.ArrayList;

/**
 * Начальное состояние списка опций ТС.
 */
final class VehicleOptionsViewStateInitial implements ViewState<VehicleOptionsViewActions> {

  @Override
  public void apply(@NonNull VehicleOptionsViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showVehicleOptionsList(true);
    stateActions.showVehicleOptionsPending(false);
    stateActions.showVehicleOptionsListErrorMessage(false);
    stateActions
        .setVehicleOptionsListItems(new OptionsListItems(new ArrayList<>(), new ArrayList<>()));
  }
}
