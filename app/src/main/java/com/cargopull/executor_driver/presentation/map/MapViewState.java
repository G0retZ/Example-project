package com.cargopull.executor_driver.presentation.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида карты с тепловой картой.
 */
public final class MapViewState implements ViewState<MapViewActions> {

  @Nullable
  private final String geoJson;

  MapViewState(@Nullable String geoJson) {
    this.geoJson = geoJson;
  }

  @Override
  public void apply(@NonNull MapViewActions stateActions) {
    stateActions.updateHeatMap(geoJson);
  }

  @Override
  public String toString() {
    return "MapViewState{" +
        "geoJson='" + geoJson + '\'' +
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

    MapViewState that = (MapViewState) o;

    return geoJson != null ? geoJson.equals(that.geoJson) : that.geoJson == null;
  }

  @Override
  public int hashCode() {
    return geoJson != null ? geoJson.hashCode() : 0;
  }
}
