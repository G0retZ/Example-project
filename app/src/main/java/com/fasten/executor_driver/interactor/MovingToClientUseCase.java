package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Юзкейс движения к клиенту. Слушает заказ из гейтвея, передает действия исполнителя.
 */
interface MovingToClientUseCase {

  /**
   * Запрашивает данные о выполняемом заказе.
   *
   * @return {@link Flowable<Order>} результат запроса.
   */
  Flowable<Order> getOrders();

  /**
   * Запрашивает звонок клиенту.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable callToClient();

  /**
   * Сообщает серверу о прибытии к клиенту.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable reportArrival();
}
