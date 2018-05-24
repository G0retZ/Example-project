package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Гейтвей цены выполнения заказа.
 */
public interface OrderExcessCostGateway {

  /**
   * Запрашивает изменения цены выполняемого заказа.
   *
   * @return {@link Flowable<Integer>} результат запроса.
   */
  @NonNull
  Flowable<Integer> getOrderExcessCost();
}
