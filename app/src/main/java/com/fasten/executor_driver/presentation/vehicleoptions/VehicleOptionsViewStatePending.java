package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания занятия ТС с выбранными опциями.
 */
public final class VehicleOptionsViewStatePending implements ViewState<VehicleOptionsViewActions> {

  @Override
  public void apply(@NonNull VehicleOptionsViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showVehicleOptionsList(true);
    stateActions.showVehicleOptionsPending(true);
    stateActions.showVehicleOptionsListErrorMessage(false);
  }
}
