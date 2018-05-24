package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
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
