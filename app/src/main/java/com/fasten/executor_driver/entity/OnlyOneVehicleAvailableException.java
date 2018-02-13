package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Исключение о доступности только одного ТС.
 */
public class OnlyOneVehicleAvailableException extends IOException {

  public OnlyOneVehicleAvailableException() {
    super();
  }

  @SuppressWarnings("unused")
  public OnlyOneVehicleAvailableException(String message) {
    super(message);
  }

  @SuppressWarnings("unused")
  public OnlyOneVehicleAvailableException(String message, Throwable cause) {
    super(message, cause);
  }

  @SuppressWarnings("unused")
  public OnlyOneVehicleAvailableException(Throwable cause) {
    super(cause);
  }
}
