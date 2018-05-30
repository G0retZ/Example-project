package com.fasten.executor_driver.interactor;

import com.fasten.executor_driver.entity.Order;
import io.reactivex.Flowable;

/**
 * Юзкейс заказа. Слушает заказ из гейтвея.
 */
public interface OrderUseCase {

  /**
   * Запрашивает данные о выполняемом заказе.
   *
   * @return {@link Flowable<Order>} результат запроса.
   */
  Flowable<Order> getOrders();
}
