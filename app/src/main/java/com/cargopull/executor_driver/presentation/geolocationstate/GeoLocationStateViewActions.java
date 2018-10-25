package com.cargopull.executor_driver.presentation.geolocationstate;

/**
 * Действия для смены состояния вида состояния сервисов местоположения.
 */
public interface GeoLocationStateViewActions {

  /**
   * Показать состояние сревисов местоположения.
   *
   * @param available - доступность сервисов местоположения.
   */
  void showGeolocationState(boolean available);
}
