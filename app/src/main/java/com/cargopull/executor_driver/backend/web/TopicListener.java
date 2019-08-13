package com.cargopull.executor_driver.backend.web;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import io.reactivex.Flowable;

/**
 * Получение сообщений из WebSocket.
 */
public interface TopicListener {

  /*
   * Запрос сообщений от сервера.
   * Получаемые сообщения автоматически подтверждены.
   * Подписка идёт на заданый топик одна на всех слушателей, отписка при отписке всех слушателей.
   * Ошибки подписчикам не передаются. Сообщения не кешируются.
   *
   * @return
   */
  @NonNull
  Flowable<StompFrame> getMessages();
}
