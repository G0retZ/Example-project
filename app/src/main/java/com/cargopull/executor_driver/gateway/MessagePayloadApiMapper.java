package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Извлекаем сообщение сервера.
 */
public class MessagePayloadApiMapper implements Mapper<StompMessage, String> {

  @NonNull
  @Override
  public String map(@NonNull StompMessage from) {
    return from.getPayload().replace("\"", "").trim();
  }
}
