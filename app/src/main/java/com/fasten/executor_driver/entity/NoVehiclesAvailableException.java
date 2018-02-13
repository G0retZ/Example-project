package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Исключение об отстустствии доступных ТС.
 */
public class NoVehiclesAvailableException extends IOException {

  public NoVehiclesAvailableException() {
    super();
  }

  @SuppressWarnings("unused")
  public NoVehiclesAvailableException(String message) {
    super(message);
  }

  @SuppressWarnings("unused")
  public NoVehiclesAvailableException(String message, Throwable cause) {
    super(message, cause);
  }

  @SuppressWarnings("unused")
  public NoVehiclesAvailableException(Throwable cause) {
    super(cause);
  }
}
