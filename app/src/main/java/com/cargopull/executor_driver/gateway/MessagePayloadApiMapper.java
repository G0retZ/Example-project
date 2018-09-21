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
    String payload = from.getPayload();
    int stringLength = payload.length();
    if (stringLength > 1 && payload.charAt(0) == '"' && payload.charAt(stringLength - 1) == '"') {
      payload = payload.substring(1, stringLength - 1);
    }
    return payload.trim();
  }
}
