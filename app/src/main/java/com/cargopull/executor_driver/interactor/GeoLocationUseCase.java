package com.cargopull.executor_driver.interactor;

import com.cargopull.executor_driver.entity.GeoLocation;
import io.reactivex.Flowable;

/**
 * Юзкейс запросов местоположений. Слушает местопложения из гейтвея, и публикует получаемые значения
 * на сервер.
 */
public interface GeoLocationUseCase {

  /**
   * Запрашивает сбор данных о местоположении.
   *
   * @return {@link Flowable<GeoLocation>} результат запроса.
   */
  Flowable<GeoLocation> getGeoLocations();
}
