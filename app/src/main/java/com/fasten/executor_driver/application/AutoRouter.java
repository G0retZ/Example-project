package com.fasten.executor_driver.application;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationNavigate;

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
