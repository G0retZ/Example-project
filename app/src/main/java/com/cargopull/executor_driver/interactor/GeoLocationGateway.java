package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.GeoLocation;
import io.reactivex.Flowable;

/**
 * Гейтвей получения данных о местоположении.
 */
public interface GeoLocationGateway {

  /**
   * Запрашивает геопозиции у центра геолокации.
   *
   * @param interval максимальный интервал между значениями.
   * @return {@link Flowable<GeoLocation>} полученные геопозиции.
   */
  @NonNull
  Flowable<GeoLocation> getGeoLocations(long interval);
}
