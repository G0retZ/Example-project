package com.cargopull.executor_driver.gateway;

import io.reactivex.functions.Predicate;
import ua.naiksoftware.stomp.client.StompMessage;

public class OrderCostDetailsFilter implements Predicate<StompMessage> {

  @Override
  public boolean test(StompMessage stompMessage) {
    return "PAYMENT_CONFIRMATION".equals(stompMessage.findHeader("Status"));
  }
}
