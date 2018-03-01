package com.fasten.executor_driver.presentation.options;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания занятия ТС с выбранными опциями.
 */
public final class OptionsViewStatePending implements ViewState<OptionsViewActions> {

  @Override
  public void apply(@NonNull OptionsViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showVehicleOptionsList(false);
    stateActions.showVehicleOptionsPending(true);
    stateActions.showVehicleOptionsListErrorMessage(false);
  }
}
