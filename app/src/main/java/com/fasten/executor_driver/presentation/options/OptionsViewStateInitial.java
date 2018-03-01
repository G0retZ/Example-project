package com.fasten.executor_driver.presentation.options;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import java.util.ArrayList;

/**
 * Начальное состояние списка опций ТС.
 */
public final class OptionsViewStateInitial implements ViewState<OptionsViewActions> {

  @Override
  public void apply(@NonNull OptionsViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showVehicleOptionsList(true);
    stateActions.showVehicleOptionsPending(false);
    stateActions.showVehicleOptionsListErrorMessage(false);
    stateActions
        .setVehicleOptionsListItems(new OptionsListItems(new ArrayList<>(), new ArrayList<>()));
  }
}
