package com.cargopull.executor_driver.presentation.geolocationstate;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ImageTextViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние готовности настроек геолокации.
 */
final class GeoLocationStateReadyViewState implements ViewState<ImageTextViewActions> {

  @Override
  public void apply(@NonNull ImageTextViewActions stateActions) {
    stateActions.setVisible(-1, false);
  }
}
