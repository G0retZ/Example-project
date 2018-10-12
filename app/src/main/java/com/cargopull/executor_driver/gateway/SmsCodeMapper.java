package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
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
