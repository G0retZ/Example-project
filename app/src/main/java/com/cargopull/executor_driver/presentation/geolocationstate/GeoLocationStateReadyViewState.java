package com.cargopull.executor_driver.presentation.geolocationstate;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние готовности настроек геолокации.
 */
final class GeoLocationStateReadyViewState implements ViewState<ViewActions> {

  @Override
  public void apply(@NonNull ViewActions stateActions) {
    stateActions.setVisible(-1, false);
  }
}
