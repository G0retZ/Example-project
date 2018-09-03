package com.cargopull.executor_driver.gateway;

import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import io.reactivex.functions.Predicate;
import ua.naiksoftware.stomp.client.StompMessage;

public class PreOrderFilter implements Predicate<StompMessage> {

  @Override
  public boolean test(StompMessage stompMessage) throws Exception {
    if ("true".equals(stompMessage.findHeader("PreliminaryExpired"))) {
      String payload = stompMessage.getPayload();
      throw new OrderOfferExpiredException(payload != null ? payload.replace("\"", "").trim() : "");
    }
    return stompMessage.findHeader("Preliminary") != null;
  }
}
