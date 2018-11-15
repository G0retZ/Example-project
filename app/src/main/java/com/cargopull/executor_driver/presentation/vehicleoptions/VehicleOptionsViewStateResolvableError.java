package com.cargopull.executor_driver.presentation.vehicleoptions;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при занятии ТС с опциями.
 */
public final class VehicleOptionsViewStateResolvableError implements
    ViewState<VehicleOptionsViewActions> {

  @StringRes
  private final int errorMessage;
  @NonNull
  private final ViewState<VehicleOptionsViewActions> parentViewState;
  @NonNull
  private final Runnable consumeAction;

  VehicleOptionsViewStateResolvableError(@StringRes int errorMessage,
      @NonNull ViewState<VehicleOptionsViewActions> parentViewState,
      @NonNull Runnable consumeAction) {
    this.errorMessage = errorMessage;
    this.parentViewState = parentViewState;
    this.consumeAction = consumeAction;
  }

  @Override
  public void apply(@NonNull VehicleOptionsViewActions stateActions) {
    parentViewState.apply(stateActions);
    stateActions.showPersistentDialog(errorMessage, consumeAction);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    VehicleOptionsViewStateResolvableError that = (VehicleOptionsViewStateResolvableError) o;

    if (errorMessage != that.errorMessage) {
      return false;
    }
    return parentViewState.equals(that.parentViewState);
  }

  @Override
  public int hashCode() {
    int result = errorMessage;
    result = 31 * result + parentViewState.hashCode();
    return result;
  }
}
