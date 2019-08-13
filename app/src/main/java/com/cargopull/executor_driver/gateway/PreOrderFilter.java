package com.cargopull.executor_driver.gateway;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import io.reactivex.functions.Predicate;

public class PreOrderFilter implements Predicate<StompFrame> {

  @Override
  public boolean test(StompFrame stompFrame) throws Exception {
    if ("true".equals(stompFrame.getHeaders().get("PreliminaryExpired"))) {
      String payload = stompFrame.getBody();
      throw new OrderOfferExpiredException(payload != null ? payload.replace("\"", "").trim() : "");
    }
    if ("true".equals(stompFrame.getHeaders().get("PreliminaryCancelled"))) {
      String payload = stompFrame.getBody();
      throw new OrderCancelledException(payload != null ? payload.replace("\"", "").trim() : "");
    }
    return stompFrame.getHeaders().get("Preliminary") != null;
  }
}
