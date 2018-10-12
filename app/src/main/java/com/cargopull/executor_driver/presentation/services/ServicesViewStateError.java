package com.cargopull.executor_driver.presentation.services;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние непоправимой ошибки при загрузке услуг.
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
    stateActions.showServicesListErrorMessage(true, errorMessage);
    stateActions.showServicesListResolvableErrorMessage(false, 0);
  }

  @Override
  public String toString() {
    return "ServicesViewStateError{" +
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
