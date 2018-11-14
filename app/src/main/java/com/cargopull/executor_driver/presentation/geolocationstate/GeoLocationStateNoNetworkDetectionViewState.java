package com.cargopull.executor_driver.presentation.geolocationstate;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние недоступности обнаружения по Wi-Fi и мобильным сетям.
 */
final class GeoLocationStateNoNetworkDetectionViewState implements
    ViewState<GeoLocationStateViewActions> {

  @Override
  public void apply(@NonNull GeoLocationStateViewActions stateActions) {
    stateActions.setVisible(-1, true);
    stateActions.setImage(R.id.geoIcon, R.drawable.ic_network_detection_icon);
    stateActions.setText(R.id.titleText, R.string.turn_on_geo_detection);
    stateActions.setText(R.id.explainText, R.string.network_detection_required);
  }
}
