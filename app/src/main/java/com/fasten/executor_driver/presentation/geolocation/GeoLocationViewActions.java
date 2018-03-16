package com.fasten.executor_driver.presentation.geolocation;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.GeoLocation;

/**
 * Действия для смены состояния вида местоположения.
 */
public interface GeoLocationViewActions {

  /**
   * Обновить отображение местоположения.
   *
   * @param geoLocation - данные местоположения на карте.
   */
  void updateLocation(@NonNull GeoLocation geoLocation);
}
