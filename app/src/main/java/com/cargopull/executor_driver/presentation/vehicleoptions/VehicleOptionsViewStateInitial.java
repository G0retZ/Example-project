package com.cargopull.executor_driver.presentation.vehicleoptions;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;
import java.util.ArrayList;

/**
 * Начальное состояние списка опций ТС.
 */
public final class VehicleOptionsViewStateInitial implements ViewState<VehicleOptionsViewActions> {

  @Override
  public void apply(@NonNull VehicleOptionsViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showVehicleOptionsList(true);
    stateActions.showVehicleOptionsPending(false);
    stateActions.showVehicleOptionsListErrorMessage(false);
    stateActions.setVehicleOptionsListItems(
        new VehicleOptionsListItems(new ArrayList<>(), new ArrayList<>())
    );
    // Убираем диалог
    stateActions.dismissDialog();
  }
}
