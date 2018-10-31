package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.RoutePoint;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;

/**
 * Юзкейс маршрута выполнения заказа.
 */
public interface OrderRouteUseCase {

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
   * Завершить заказ.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable completeTheOrder();

  /**
   * Задать следующую маршрутную точку.
   *
   * @param routePoint - точка маршрута
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable nextRoutePoint(@NonNull RoutePoint routePoint);
}
