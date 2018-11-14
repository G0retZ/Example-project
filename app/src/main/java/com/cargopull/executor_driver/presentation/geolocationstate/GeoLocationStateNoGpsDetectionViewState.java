package com.cargopull.executor_driver.presentation.geolocationstate;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ImageTextViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние недоступности обнаружения по GPS.
 */
final class GeoLocationStateNoGpsDetectionViewState implements ViewState<ImageTextViewActions> {

  @Override
  public void apply(@NonNull ImageTextViewActions stateActions) {
    stateActions.setVisible(-1, true);
    stateActions.setImage(R.id.geoIcon, R.drawable.ic_gps_detection_icon);
    stateActions.setText(R.id.titleText, R.string.turn_on_geo_detection);
    stateActions.setText(R.id.explainText, R.string.gps_detection_required);
  }
}
