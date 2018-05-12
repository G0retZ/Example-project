package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Гейтвей работы с заказами при движении к клиенту.
 */
interface MovingToClientGateway {

  /**
   * Ожидает от сокета заказы для отображения.
   *
   * @return {@link Flowable<Order>} заказы для исполнителя.
   */
  @NonNull
  Flowable<Order> getOrders();

  /**
   * Передает запрос звонка клиенту.
   *
   * @param order заказ, к которому относится запрос звонка.
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable callToClient(@NonNull Order order);

  /**
   * Передает сообщение о прибытии к клиенту.
   *
   * @param order заказ, к которому относится сообщение о прибытии.
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable reportArrival(@NonNull Order order);
}
