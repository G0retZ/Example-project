package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Юзкейс общего времени выполнения заказа.
 */
public interface OrderFulfillmentTimeUseCase {

  /**
   * Запрашивает общее время выполнения заказа.
   *
   * @return {@link Flowable<Long>} результат запроса.
   */
  @NonNull
  Flowable<Long> getOrderElapsedTime();
}
