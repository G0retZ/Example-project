package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Flowable;
import java.util.List;

/**
 * Юзкейс заказа. Слушает принятые заказы из гейтвея, а так же добавляет и удаляет заказы в список задач.
 */
public interface PreOrdersUseCase {

  /**
   * Запрашивает данные приянтых заказах.
   *
   * @return {@link Flowable<List>} результат запроса.
   */
  @NonNull
  Flowable<List<Order>> getPreOrders();

  /**
   * Сообщает, что заказ более не запланирован, чтобы все подписчики обновили свое состояние.
   * Нужно для случаев, когда водитель отказался или выполнил предзаказ.
   */
  void unSchedulePreOrder(@NonNull Order order);

  /**
   * Сообщает, что новый заказ запланирован, чтобы все подписчики обновили свое состояние.
   * Нужно для случаев, когда водитель отказался или выполнил предзаказ.
   */
  void schedulePreOrder(@NonNull Order order);
}
