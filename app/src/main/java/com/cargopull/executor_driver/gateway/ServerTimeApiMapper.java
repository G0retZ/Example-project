package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Извлекаем время сервера из ответа сервера.
 */
public class ServerTimeApiMapper implements Mapper<StompMessage, Long> {

  @Inject
  public ServerTimeApiMapper() {
  }

  @NonNull
  @Override
  public Long map(@NonNull StompMessage from) throws Exception {
    try {
      return Long.valueOf(from.findHeader("ServerTimeStamp"));
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: неверный формат стоимости!", e);
    }
  }
}
