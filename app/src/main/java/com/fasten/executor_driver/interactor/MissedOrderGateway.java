package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Гейтвей упущенноых заказов.
 */
interface MissedOrderGateway {

  /**
   * Получать сообщения об упущенных заказах.
   *
   * @param channelId - ID "канала", откуда брать сообщения.
   * @return {@link Flowable<String>} результат запроса.
   */
  @NonNull
  Flowable<String> loadMissedOrdersMessages(@NonNull String channelId);
}
