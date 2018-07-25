package com.cargopull.executor_driver.backend.geolocation;

import android.location.Location;
import io.reactivex.Flowable;

/**
 * Центр геолокации.
 */
public interface GeolocationCenter {

  /**
   * Начать получать геопозицию.
   *
   * @param maxInterval максимальный интервал между значениями - значения будут приходить с этим
   * интервалом или быстрее.
   * @return {@link Flowable<Location>} полученные геопозиции.
   */
  Flowable<Location> getLocations(long maxInterval);
}
