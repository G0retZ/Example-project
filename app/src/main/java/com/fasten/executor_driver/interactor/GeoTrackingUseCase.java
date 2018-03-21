package com.fasten.executor_driver.interactor;

import io.reactivex.Completable;

/**
 * Юзкейс запросов местоположений. Слушает местопложения из гейтвея, и публикует получаемые
 * значения.
 */
public interface GeoTrackingUseCase {

  /**
   * Переинициирует передачу данных о местоположении.
   */
  Completable reload();
}
