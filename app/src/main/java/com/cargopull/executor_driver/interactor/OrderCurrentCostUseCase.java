package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Юзкейс текущей цены выполнения заказа.
 */
public interface OrderCurrentCostUseCase {

  /**
   * Запрашивает текущую цену выполняемого заказа.
   *
   * @return {@link Flowable<Long>} результат запроса.
   */
  @NonNull
  Flowable<Long> getOrderCurrentCost();
}
