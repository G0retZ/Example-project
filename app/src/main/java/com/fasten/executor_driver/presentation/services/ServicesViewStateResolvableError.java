package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Состояние поправимой ошибки при отправки услуг.
 */
public final class ServicesViewStateResolvableError extends ServicesViewState {

  @StringRes
  private final int errorMessage;

  ServicesViewStateResolvableError(@StringRes int errorMessage,
      ServicesViewState servicesViewState) {
    super(servicesViewState);
    this.errorMessage = errorMessage;
  }

  @Override
  public void apply(@NonNull ServicesViewActions stateActions) {
    super.apply(stateActions);
    stateActions.enableReadyButton(false);
    stateActions.showServicesList(true);
    stateActions.showServicesPending(false);
    stateActions.showServicesListErrorMessage(false, 0);
    stateActions.showServicesListResolvableErrorMessage(true, errorMessage);
  }

  @Override
  public String toString() {
    return "ServicesViewStateResolvableError{" +
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
    if (!super.equals(o)) {
      return false;
    }

    ServicesViewStateResolvableError that = (ServicesViewStateResolvableError) o;

    return errorMessage == that.errorMessage;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + errorMessage;
    return result;
  }
}
