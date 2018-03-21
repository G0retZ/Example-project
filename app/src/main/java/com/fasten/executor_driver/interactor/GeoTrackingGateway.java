package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.GeoLocation;
import io.reactivex.Completable;

/**
 * Гейтвей передачи серверу данных о местоположении.
 */
public interface GeoTrackingGateway {

  /**
   * Передает данные геопозиции серверу.
   *
   * @param geoLocation геопозиция для передачи.
   * @return {@link Completable} результат передачи.
   */
  @NonNull
  Completable sendGeoLocation(GeoLocation geoLocation);
}
