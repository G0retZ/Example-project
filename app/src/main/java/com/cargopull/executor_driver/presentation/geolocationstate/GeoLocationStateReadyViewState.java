package com.cargopull.executor_driver.presentation.geolocationstate;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние готовности настроек геолокации.
 */
final class GeoLocationStateReadyViewState implements ViewState<GeoLocationStateViewActions> {

  @Override
  public void apply(@NonNull GeoLocationStateViewActions stateActions) {
    stateActions.setVisible(-1, false);
  }
}
