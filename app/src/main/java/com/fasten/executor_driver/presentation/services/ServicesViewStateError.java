package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import java.util.ArrayList;

/**
 * Состояние непоправимой ошибки при загрузке услуг.
 */
public final class ServicesViewStateError extends ServicesViewState {

  @StringRes
  private final int errorMessage;

  ServicesViewStateError(@StringRes int errorMessage) {
    super(new ArrayList<>());
    this.errorMessage = errorMessage;
  }

  @Override
  public void apply(@NonNull ServicesViewActions stateActions) {
    super.apply(stateActions);
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
    if (!super.equals(o)) {
      return false;
    }

    ServicesViewStateError that = (ServicesViewStateError) o;

    return errorMessage == that.errorMessage;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + errorMessage;
    return result;
  }
}
