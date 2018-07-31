package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Гейтвей получения текущего времени сервера.
 */
public interface ServerTimeGateway {

  /**
   * Получать текущие временные метки сервера.
   *
   * @param channelId - ID "канала", откуда брать временные метки.
   * @return {@link Flowable<Long>} результат запроса.
   */
  @NonNull
  Flowable<Long> loadServerTime(@NonNull String channelId);
}
