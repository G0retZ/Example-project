package com.fasten.executor_driver.presentation.geolocation;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки в данных с сервера.
 */
class GeoLocationViewStateServerDataError implements ViewState<GeoLocationViewActions> {

  @Nullable
  private final ViewState<GeoLocationViewActions> parentViewState;

  GeoLocationViewStateServerDataError(
      @Nullable ViewState<GeoLocationViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  @CallSuper
  public void apply(@NonNull GeoLocationViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showGeoLocationServerDataError();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GeoLocationViewStateServerDataError that = (GeoLocationViewStateServerDataError) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
