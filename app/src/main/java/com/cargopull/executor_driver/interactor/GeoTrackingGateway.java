package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.GeoLocation;
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
