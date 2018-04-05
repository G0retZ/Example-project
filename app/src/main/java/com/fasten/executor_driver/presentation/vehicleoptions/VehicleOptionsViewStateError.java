package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при занятии ТС с опциями.
 */
public final class VehicleOptionsViewStateError implements ViewState<VehicleOptionsViewActions> {

  @StringRes
  private final int errorMessage;

  VehicleOptionsViewStateError(@StringRes int errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public void apply(@NonNull VehicleOptionsViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showVehicleOptionsList(false);
    stateActions.showVehicleOptionsPending(false);
    stateActions.showVehicleOptionsListErrorMessage(true);
    stateActions.setVehicleOptionsListErrorMessage(errorMessage);
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

    VehicleOptionsViewStateError that = (VehicleOptionsViewStateError) o;

    return errorMessage == that.errorMessage;
  }

  @Override
  public int hashCode() {
    return errorMessage;
  }
}
