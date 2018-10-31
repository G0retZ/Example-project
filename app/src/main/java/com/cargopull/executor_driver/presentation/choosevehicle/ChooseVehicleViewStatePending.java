package com.cargopull.executor_driver.presentation.choosevehicle;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания загрузки списка ТС.
 */
final class ChooseVehicleViewStatePending implements ViewState<ChooseVehicleViewActions> {

  @Override
  public void apply(@NonNull ChooseVehicleViewActions stateActions) {
    stateActions.showVehicleList(false);
    stateActions.showVehicleListErrorMessage(false);
    stateActions.showVehicleListPending(true);
  }
}
