package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Гейтвей получения сообщений о новой версии.
 */
interface UpdateMessageGateway {

  /**
   * Получать сообщения о доступности новой версии.
   *
   * @param channelId - ID "канала", откуда брать причины отказа.
   * @return {@link Flowable<String>} результат запроса.
   */
  @NonNull
  Flowable<String> loadUpdateMessages(@NonNull String channelId);
}
