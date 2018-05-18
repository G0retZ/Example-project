package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Flowable;

/**
 * Гейтвей получения с заказа.
 */
public interface OrderGateway {

  /**
   * Ожидает заказы для исполнителя у сокета.
   *
   * @param executorState - фильтр по статусу, от которого ждать заказа.
   * @return {@link Flowable<Order>} заказы для исполнителя.
   */
  @NonNull
  Flowable<Order> getOrders(@NonNull ExecutorState executorState);
}
