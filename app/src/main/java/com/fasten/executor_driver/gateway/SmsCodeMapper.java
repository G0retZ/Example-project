package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import javax.inject.Inject;

public class SmsCodeMapper implements Mapper<String, String> {

  @Inject
  public SmsCodeMapper() {
  }

  @NonNull
  @Override
  public String map(@NonNull String from) {
    return from;
  }
}
