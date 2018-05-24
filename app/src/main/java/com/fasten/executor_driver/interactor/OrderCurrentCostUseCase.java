package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Юзкейс текущей цены выполнения заказа.
 */
interface OrderCurrentCostUseCase {

  /**
   * Запрашивает текущую цену выполняемого заказа.
   *
   * @return {@link Flowable<Integer>} результат запроса.
   */
  @NonNull
  Flowable<Integer> getOrderCurrentCost();
}
