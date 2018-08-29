package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем текст из хедера ответа сервера в число.
 */
public class ServerTimeApiMapper implements Mapper<StompMessage, Long> {

  @NonNull
  @Override
  public Long map(@NonNull StompMessage from) throws Exception {
    try {
      return Long.valueOf(from.findHeader("ServerTimeStamp"));
    } catch (Throwable t) {
      throw new DataMappingException(t);
    }
  }
}
