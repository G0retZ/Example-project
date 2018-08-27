package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Гейтвей получения сообщений о новой версии.
 */
public interface UpdateMessageGateway {

  /**
   * Получать сообщения о доступности новой версии.
   *
   * @return {@link Flowable<String>} результат запроса.
   */
  @NonNull
  Flowable<String> loadUpdateMessages();
}
