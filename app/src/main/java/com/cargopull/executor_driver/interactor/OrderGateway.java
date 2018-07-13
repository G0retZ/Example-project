package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Flowable;

/**
 * Гейтвей получения с заказа.
 */
public interface OrderGateway {

  /**
   * Ожидает заказы для исполнителя у сокета.
   *
   * @return {@link Flowable<Order>} заказы для исполнителя.
   */
  @NonNull
  Flowable<Order> getOrders();
}
