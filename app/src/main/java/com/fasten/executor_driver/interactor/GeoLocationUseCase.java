package com.fasten.executor_driver.interactor;

/**
 * Юзкейс запросов местоположений. Слушает местопложения из гейтвея, и публикует получаемые
 * значения.
 */
public interface GeoLocationUseCase {

  /**
   * Переинициирует сбор данных о местоположении.
   */
  void reload();
}
