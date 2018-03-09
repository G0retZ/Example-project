package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;
import javax.inject.Inject;

public class SmsCodeExtractor implements CodeExtractor {

  @Inject
  public SmsCodeExtractor() {
  }

  @Nullable
  @Override
  public String extractCode(@Nullable String message) {
    return message;
  }
}
