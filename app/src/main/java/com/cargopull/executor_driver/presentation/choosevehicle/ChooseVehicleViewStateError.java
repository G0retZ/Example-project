package com.cargopull.executor_driver.presentation.choosevehicle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при загрузке списка ТС.
 */
final class ChooseVehicleViewStateError implements ViewState<ChooseVehicleViewActions> {

  @StringRes
  private final int errorMessage;

  ChooseVehicleViewStateError(@StringRes int errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public void apply(@NonNull ChooseVehicleViewActions stateActions) {
    stateActions.showVehicleList(false);
    stateActions.showVehicleListPending(false);
    stateActions.showVehicleListErrorMessage(true);
    stateActions.setVehicleListErrorMessage(errorMessage);
  }

  @Override
  public String toString() {
    return "ChooseVehicleViewStateError{" +
        "errorMessage=" + errorMessage +
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

    ChooseVehicleViewStateError that = (ChooseVehicleViewStateError) o;

    return errorMessage == that.errorMessage;
  }

  @Override
  public int hashCode() {
    return errorMessage;
  }
}
