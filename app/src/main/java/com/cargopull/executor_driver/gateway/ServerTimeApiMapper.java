package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;

import com.cargopull.executor_driver.backend.stomp.StompFrame;

import javax.inject.Inject;

/**
 * Извлекаем время сервера из ответа сервера.
 */
public class ServerTimeApiMapper implements Mapper<StompFrame, Long> {

  @Inject
  public ServerTimeApiMapper() {
  }

  @NonNull
  @Override
  public Long map(@NonNull StompFrame from) throws Exception {
    try {
      return Long.valueOf(from.getHeaders().get("ServerTimeStamp"));
    } catch (Exception e) {
      throw new DataMappingException("Mapping error: wrong format of cost!", e);
    }
  }
}
