package com.cargopull.executor_driver.gateway;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import io.reactivex.functions.Predicate;

public class PreOrdersListFilter implements Predicate<StompFrame> {

  @Override
  public boolean test(StompFrame stompFrame) {
    return stompFrame.getHeaders().get("PreliminaryOrderList") != null;
  }
}
