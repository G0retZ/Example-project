package com.cargopull.executor_driver.presentation.geolocationstate;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна состояния сервисов местоположения.
 */
public interface GeoLocationStateViewModel extends ViewModel<GeoLocationStateViewActions> {

  /**
   * Запрашивает повторную проверку настроек.
   */
  void checkSettings();
}
