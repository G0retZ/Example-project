package com.fasten.executor_driver.utils;

import javax.inject.Inject;

public class TimeUtilsImpl implements TimeUtils {

  @Inject
  public TimeUtilsImpl() {
  }

  @Override
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }
}
