package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.RoutePoint;
import io.reactivex.Completable;

/**
 * Гейтвей работы с точками маршрута.
 */
public interface OrderRouteGateway {

  /**
   * Запрашивает сервер закрыть точку.
   *
   * @param routePoint - точка маршрута.
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable closeRoutePoint(@NonNull RoutePoint routePoint);

  /**
   * Запрашивает сервер задать следующую точку маршрута.
   *
   * @param routePoint - точка маршрута.
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable nextRoutePoint(@NonNull RoutePoint routePoint);
}
