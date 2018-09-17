package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Юзкейс сообщений для оповещений заказов.
 */
public interface NotificationMessageUseCase {

  /**
   * Запрашивает сообщения для оповещений.
   *
   * @return {@link Flowable<String>} результат запроса.
   */
  @NonNull
  Flowable<String> getNotificationMessages();
}
