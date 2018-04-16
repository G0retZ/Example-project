package com.fasten.executor_driver.utils;

@SuppressWarnings("unused")
class TimeUtilsImpl implements TimeUtils {

  @Override
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }
}
