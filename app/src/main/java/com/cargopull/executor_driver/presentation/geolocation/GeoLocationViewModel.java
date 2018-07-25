package com.cargopull.executor_driver.presentation.geolocation;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна местоположения.
 */
public interface GeoLocationViewModel extends ViewModel<GeoLocationViewActions> {

  /**
   * Начать получть геопозиции.
   */
  void updateGeoLocations();
}
