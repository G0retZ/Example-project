package com.fasten.executor_driver.presentation.options;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при занятии ТС с опциями.
 */
public final class OptionsViewStateError implements ViewState<OptionsViewActions> {

  @StringRes
  private final int errorMessage;

  public OptionsViewStateError(@StringRes int errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public void apply(@NonNull OptionsViewActions stateActions) {
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

    OptionsViewStateError that = (OptionsViewStateError) o;

    return errorMessage == that.errorMessage;
  }

  @Override
  public int hashCode() {
    return errorMessage;
  }
}
