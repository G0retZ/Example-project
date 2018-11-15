package com.cargopull.executor_driver.gateway;

import io.reactivex.functions.Predicate;
import java.util.Map;

public class MessageFcmFilter implements Predicate<Map<String, String>> {

  @Override
  public boolean test(Map<String, String> map) {
    return "ANNOUNCEMENT".equals(map.get("action"));
  }
}
