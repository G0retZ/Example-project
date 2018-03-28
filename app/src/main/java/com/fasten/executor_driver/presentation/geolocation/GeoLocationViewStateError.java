package com.fasten.executor_driver.presentation.geolocation;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние вида ошибки местоположения.
 */
public final class GeoLocationViewStateError implements ViewState<GeoLocationViewActions> {

  @Override
  public void apply(@NonNull GeoLocationViewActions stateActions) {
    stateActions.showGeoLocationError(true);
  }
}
