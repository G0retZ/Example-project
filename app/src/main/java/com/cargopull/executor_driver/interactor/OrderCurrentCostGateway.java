package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Гейтвей цены выполнения заказа.
 */
public interface OrderCurrentCostGateway {

  /**
   * Запрашивает изменения цены выполняемого заказа.
   *
   * @param channelId - ID "канала", откуда брать баланс.
   * @return {@link Flowable<Integer>} результат запроса.
   */
  @NonNull
  Flowable<Integer> getOrderCurrentCost(@NonNull String channelId);
}
