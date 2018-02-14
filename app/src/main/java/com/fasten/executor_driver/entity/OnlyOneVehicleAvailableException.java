package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Исключение о доступности только одного ТС.
 */
public class OnlyOneVehicleAvailableException extends IOException {

  public OnlyOneVehicleAvailableException() {
    super();
  }
}
