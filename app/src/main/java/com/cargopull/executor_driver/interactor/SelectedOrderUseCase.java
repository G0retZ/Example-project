package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Юзкейс выбранного заказа. Слушает юзкейс списка заказов, и отображает выбранный, если он есть в списке.
 */
public interface SelectedOrderUseCase {

  /**
   * Запрашивает данные о выбранном заказе.
   *
   * @return {@link Flowable<Order>} результат запроса.
   */
  @NonNull
  Flowable<Order> getSelectedOrder();

  /**
   * Сообщает о желаемом выборе заказа, чтобы все подписчики обновили свое состояние.
   * Есди желаемого заказа нет в списке, то подписчики получают ошибку.
   *
   * @param order - выбранный заказ.
   * @return {@link Completable} результат запроса.
   */
  Completable setSelectedOrder(@NonNull Order order);
}
