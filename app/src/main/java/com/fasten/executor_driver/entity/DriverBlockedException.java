package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Ошибка: водитель заблокирован.
 */
public class DriverBlockedException extends IOException {

  public DriverBlockedException() {
    super();
  }

  @SuppressWarnings("SameParameterValue")
  public DriverBlockedException(String message) {
    super(message);
  }
}
