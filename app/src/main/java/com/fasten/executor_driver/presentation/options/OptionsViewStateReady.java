package com.fasten.executor_driver.presentation.options;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние гоновности списка опций ТС.
 */
public final class OptionsViewStateReady implements ViewState<OptionsViewActions> {

  @NonNull
  private final OptionsListItems optionsListItems;

  public OptionsViewStateReady(@NonNull OptionsListItems optionsListItems) {
    this.optionsListItems = optionsListItems;
  }

  @Override
  public void apply(@NonNull OptionsViewActions stateActions) {
    stateActions.enableReadyButton(true);
    stateActions.showVehicleOptionsList(true);
    stateActions.showVehicleOptionsPending(false);
    stateActions.showVehicleOptionsListErrorMessage(false);
    stateActions.setVehicleOptionsListItems(optionsListItems);
  }

  @Override
  public String toString() {
    return "OptionsViewStateInitial{" +
        "optionsListItems=" + optionsListItems +
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

    OptionsViewStateReady that = (OptionsViewStateReady) o;

    return optionsListItems.equals(that.optionsListItems);
  }

  @Override
  public int hashCode() {
    return optionsListItems.hashCode();
  }
}
