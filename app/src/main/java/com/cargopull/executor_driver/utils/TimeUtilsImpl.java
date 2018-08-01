package com.cargopull.executor_driver.utils;

import javax.inject.Inject;

public class TimeUtilsImpl implements TimeUtils {

  private volatile long serverTimeOffset;

  @Inject
  public TimeUtilsImpl() {
  }

  @Override
  public void setServerCurrentTime(long millis) {
    serverTimeOffset = System.currentTimeMillis() - millis;
  }

  @Override
  public long currentTimeMillis() {
    return System.currentTimeMillis() - serverTimeOffset;
  }
}
