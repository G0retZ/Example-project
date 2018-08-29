package com.cargopull.executor_driver.gateway;

import com.cargopull.executor_driver.entity.PreOrderExpiredException;
import io.reactivex.functions.Predicate;
import ua.naiksoftware.stomp.client.StompMessage;

public class PreOrderFilter implements Predicate<StompMessage> {

  @Override
  public boolean test(StompMessage stompMessage) throws Exception {
    if ("true".equals(stompMessage.findHeader("PreliminaryExpired"))) {
      throw new PreOrderExpiredException();
    }
    return stompMessage.findHeader("Preliminary") != null;
  }
}
