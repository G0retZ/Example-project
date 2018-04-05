package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при отправке услуг.
 */
public final class ServicesViewStateError implements ViewState<ServicesViewActions> {

  @StringRes
  private final int errorMessage;

  ServicesViewStateError(@StringRes int errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public void apply(@NonNull ServicesViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showServicesList(false);
    stateActions.showServicesPending(false);
    stateActions.showServicesListErrorMessage(true);
    stateActions.setServicesListErrorMessage(errorMessage);
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

    ServicesViewStateError that = (ServicesViewStateError) o;

    return errorMessage == that.errorMessage;
  }

  @Override
  public int hashCode() {
    return errorMessage;
  }
}
