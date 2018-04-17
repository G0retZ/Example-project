package com.fasten.executor_driver.utils;

public class TimeUtilsImpl implements TimeUtils {

  @Override
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }
}
