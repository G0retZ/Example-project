package com.cargopull.executor_driver.gateway;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import io.reactivex.functions.Predicate;

public class CancelledOrderFilter implements Predicate<StompFrame> {

  @Override
  public boolean test(StompFrame stompFrame) {
    String header = stompFrame.getHeaders().get("PreliminaryCancelled");
    return header != null && !header.equals("true");
  }
}
