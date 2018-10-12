package com.cargopull.executor_driver.application;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationNavigate;

/**
 * Навигатор для  автоматического перехода по изменению статуса или событиям геолокации.
 */
interface AutoRouter {

  /**
   * Перейти к активити, соответствующей новому состоянию водителя.
   *
   * @param destination направление навигации для состояния исполнителя.
   */
  void navigateTo(@NonNull @ExecutorStateNavigate @GeoLocationNavigate String destination);
}
