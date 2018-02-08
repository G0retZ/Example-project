package com.fasten.executor_driver.presentation.map;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Начальное состояние карты
 */
public final class MapViewState implements ViewState<MapViewActions> {

  @Nullable
  private final String geoJson;

  public MapViewState(@Nullable String geoJson) {
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
