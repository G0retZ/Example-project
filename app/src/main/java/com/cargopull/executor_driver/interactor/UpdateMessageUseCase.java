package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Юзкейс получения сообщений о новой версии.
 */
public interface UpdateMessageUseCase {

  /**
   * Запрашивает сообщения о доступности новой версии.
   *
   * @return {@link Flowable<String>} результат запроса.
   */
  @NonNull
  Flowable<String> getUpdateMessages();
}
