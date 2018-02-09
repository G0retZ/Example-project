package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Ошибка: водитель заблокирован.
 */

public class DriverBlockedException extends IOException {

  @SuppressWarnings("unused")
  public DriverBlockedException() {
    super();
  }

  @SuppressWarnings("SameParameterValue")
  public DriverBlockedException(String message) {
    super(message);
  }

  @SuppressWarnings("unused")
  public DriverBlockedException(String message, Throwable cause) {
    super(message, cause);
  }

  @SuppressWarnings("unused")
  public DriverBlockedException(Throwable cause) {
    super(cause);
  }
}
