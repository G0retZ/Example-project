package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Юзкейс упущенных заказов.
 */
public interface MissedOrderUseCase {

  /**
   * Запрашивает сообщения об упущенных заказах.
   *
   * @return {@link Flowable<String>} результат запроса.
   */
  @NonNull
  Flowable<String> getMissedOrders();
}
