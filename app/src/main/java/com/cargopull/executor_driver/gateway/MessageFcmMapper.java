package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import java.util.Map;

/**
 * Извлекаем текст сообщения от сервера.
 */
public class MessageFcmMapper implements Mapper<Map<String, String>, String> {

  @NonNull
  @Override
  public String map(@NonNull Map<String, String> from) {
    String message = from.get("body");
    return message != null ? message.trim() : "";
  }
}
