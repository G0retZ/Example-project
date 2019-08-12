package com.cargopull.executor_driver.gateway;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import io.reactivex.functions.Predicate;

public class UpdateMessageFilter implements Predicate<StompFrame> {

  @Override
  public boolean test(StompFrame stompFrame) {
    return "UpdateVersion".equals(stompFrame.getHeaders().get("message"));
  }
}
