package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Flowable;

/**
 * Юзкейс заказа. Слушает заказ из источника.
 */
public interface OrderUseCase {

  /**
   * Запрашивает данные о выполняемом заказе.
   *
   * @return {@link Flowable<Order>} результат запроса.
   */
  @NonNull
  Flowable<Order> getOrders();
}
