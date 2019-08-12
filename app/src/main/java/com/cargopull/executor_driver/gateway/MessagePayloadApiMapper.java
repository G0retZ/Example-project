package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.stomp.StompFrame;

/**
 * Извлекаем сообщение сервера.
 */
public class MessagePayloadApiMapper implements Mapper<StompFrame, String> {

  @NonNull
  @Override
  public String map(@NonNull StompFrame from) {
    String payload = from.getBody();
    int stringLength = payload.length();
    if (stringLength > 1 && payload.charAt(0) == '"' && payload.charAt(stringLength - 1) == '"') {
      payload = payload.substring(1, stringLength - 1);
    }
    return payload.trim();
  }
}
