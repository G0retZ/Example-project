package com.fasten.executor_driver.presentation.geolocation;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние вида местоположения.
 */
public final class GeoLocationViewState implements ViewState<GeoLocationViewActions> {

  @NonNull
  private final GeoLocation geoLocation;

  GeoLocationViewState(@NonNull GeoLocation geoLocation) {
    this.geoLocation = geoLocation;
  }

  @Override
  public void apply(@NonNull GeoLocationViewActions stateActions) {
    stateActions.updateLocation(geoLocation);
    stateActions.showGeoLocationError(false);
  }

  @Override
  public String toString() {
    return "GeoLocationViewState{" +
        "geoLocation=" + geoLocation +
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

    GeoLocationViewState that = (GeoLocationViewState) o;

    return geoLocation.equals(that.geoLocation);
  }

  @Override
  public int hashCode() {
    return geoLocation.hashCode();
  }
}
