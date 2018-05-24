package com.fasten.executor_driver.interactor;

import io.reactivex.Flowable;

/**
 * Гейтвей цены выполнения заказа.
 */
interface OrderCurrentCostGateway {

  /**
   * Запрашивает изменения цены выполняемого заказа.
   *
   * @return {@link Flowable<Integer>} результат запроса.
   */
  Flowable<Integer> getOrderCostUpdates();
}
