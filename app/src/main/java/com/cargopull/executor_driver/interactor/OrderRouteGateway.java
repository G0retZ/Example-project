package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.RoutePoint;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;

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
  Single<List<RoutePoint>> closeRoutePoint(@NonNull RoutePoint routePoint);

  /**
   * Запрашивает сервер задать следующую точку маршрута.
   *
   * @param routePoint - точка маршрута.
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Single<List<RoutePoint>> nextRoutePoint(@NonNull RoutePoint routePoint);
}
