package com.cargopull.executor_driver.gateway;

import io.reactivex.functions.Predicate;
import ua.naiksoftware.stomp.client.StompMessage;

public class ChangedOrderFilter implements Predicate<StompMessage> {

  @Override
  public boolean test(StompMessage stompMessage) {
    String header = stompMessage.findHeader("PreliminaryChanged");
    return header != null && header.equals("true");
  }
}
