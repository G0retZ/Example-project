package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Flowable;
import java.util.Set;

/**
 * Юзкейс принятых заказов. Слушает принятые заказы из гейтвея, а так же добавляет и удаляет заказы в список задач.
 */
public interface OrdersUseCase {

  /**
   * Запрашивает данные приянтых заказах.
   *
   * @return {@link Flowable<Set>} результат запроса.
   */
  @NonNull
  Flowable<Set<Order>> getOrdersSet();

  /**
   * Сообщает, что заказ более не запланирован, чтобы все подписчики обновили свое состояние.
   * Нужно для случаев, когда водитель отказался или выполнил предзаказ.
   */
  void addOrder(@NonNull Order order);

  /**
   * Сообщает, что новый заказ запланирован, чтобы все подписчики обновили свое состояние.
   * Нужно для случаев, когда водитель отказался или выполнил предзаказ.
   */
  void removeOrder(@NonNull Order order);
}
