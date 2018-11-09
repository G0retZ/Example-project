package com.cargopull.executor_driver.presentation.geolocationstate;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида заказа.
 */
final class GeoLocationStateNoLocationViewState implements ViewState<GeoLocationStateViewActions> {

  @Override
  public void apply(@NonNull GeoLocationStateViewActions stateActions) {
    stateActions.setVisible(-1, true);
    stateActions.setImage(R.id.geoIcon, R.drawable.ic_geolocation_icon);
    stateActions.setText(R.id.titleText, R.string.turn_on_geolocation);
    stateActions.setText(R.id.explainText, R.string.geolocation_required);
  }
}
