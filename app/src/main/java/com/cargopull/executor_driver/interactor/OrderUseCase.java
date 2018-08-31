package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
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
  @NonNull
  Flowable<Order> getOrders();

  /**
   * Сообщает, что заказ более не актуален, чтобы все подписчики обговили свое состояние.
   * Нужно для случаев, когда вместо сообщения от сервера обрабатывается результат принятия или отказа.
   */
  void setOrderExpired();
}
