package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.RoutePoint;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;

/**
 * Юзкейс маршрута выполнения заказа.
 */
interface OrderRouteUseCase {

  /**
   * Запрашивает данные о маршруте выполняемого заказа.
   *
   * @return {@link Flowable<List>} результат запроса.
   */
  @NonNull
  Flowable<List<RoutePoint>> getOrderRoutePoints();

  /**
   * Закрыть маршрутную точку.
   *
   * @param routePoint - точка маршрута
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable closeRoutePoint(@NonNull RoutePoint routePoint);

  /**
   * Задать следующую маршрутную точку.
   *
   * @param routePoint - точка маршрута
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable nextRoutePoint(@NonNull RoutePoint routePoint);
}
