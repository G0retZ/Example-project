package com.cargopull.executor_driver.gateway;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import io.reactivex.functions.Predicate;

public class ChangedOrderFilter implements Predicate<StompFrame> {

  @Override
  public boolean test(StompFrame stompFrame) {
    String header = stompFrame.getHeaders().get("PreliminaryChanged");
    return header != null && header.equals("true");
  }
}
