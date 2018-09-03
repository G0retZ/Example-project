package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Извлекаем сообщение сервера.
 */
// TODO: https://jira.capsrv.xyz/browse/RUCAP-1917
public class MessagePayloadApiMapper implements Mapper<StompMessage, String> {

  @NonNull
  @Override
  public String map(@NonNull StompMessage from) {
    return from.getPayload().replace("\"", "").trim();
  }
}
