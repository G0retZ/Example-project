package com.cargopull.executor_driver.gateway;

import io.reactivex.functions.Predicate;
import ua.naiksoftware.stomp.client.StompMessage;

public class UpdateMessageFilter implements Predicate<StompMessage> {

  @Override
  public boolean test(StompMessage stompMessage) {
    return "UpdateVersion".equals(stompMessage.findHeader("message"));
  }
}
