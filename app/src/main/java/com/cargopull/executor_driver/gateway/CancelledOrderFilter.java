package com.cargopull.executor_driver.gateway;

import io.reactivex.functions.Predicate;
import ua.naiksoftware.stomp.client.StompMessage;

public class CancelledOrderFilter implements Predicate<StompMessage> {

  @Override
  public boolean test(StompMessage stompMessage) {
    String header = stompMessage.findHeader("PreliminaryCancelled");
    return header != null && !header.equals("true");
  }
}
