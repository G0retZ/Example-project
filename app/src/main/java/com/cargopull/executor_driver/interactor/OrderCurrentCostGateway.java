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
   * @return {@link Flowable<Long>} результат запроса.
   */
  @NonNull
  Flowable<Long> getOrderCurrentCost(@NonNull String channelId);
}
