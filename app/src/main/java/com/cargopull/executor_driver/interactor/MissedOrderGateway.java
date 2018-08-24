package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Гейтвей упущенных заказов.
 */
public interface MissedOrderGateway {

  /**
   * Получать сообщения об упущенных заказах.
   *
   * @return {@link Flowable<String>} результат запроса.
   */
  @NonNull
  Flowable<String> loadMissedOrdersMessages();
}
