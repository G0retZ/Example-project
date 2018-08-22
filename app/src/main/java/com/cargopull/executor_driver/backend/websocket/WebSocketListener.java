package com.cargopull.executor_driver.backend.websocket;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Пролучение сообщений из WebSocket.
 */
public interface WebSocketListener {

  /*
   * Запрос сообщений от сервера.
   * Получаемые сообщения автоматически подтверждены.
   * Подписка идёт на заданый топик одна на всех слушателей, отписка при отписке всех слушателей.
   * Ошибки соединения для подписчиков не возникают, появляется ожидание соединения.
   * Сообщения не кешируются.
   *
   * @return
   */
  @NonNull
  Flowable<StompMessage> getAcknowledgedMessages();
}
