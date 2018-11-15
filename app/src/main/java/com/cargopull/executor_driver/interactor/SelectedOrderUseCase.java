package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Completable;

/**
 * Юзкейс выбранного заказа.
 */
public interface SelectedOrderUseCase {

  /**
   * Сообщает о желаемом выборе заказа, чтобы все подписчики обновили свое состояние. Есди желаемого
   * заказа нет в списке, то подписчики получают ошибку.
   *
   * @param order - выбранный заказ.
   * @return {@link Completable} результат запроса.
   */
  Completable setSelectedOrder(@NonNull Order order);
}
