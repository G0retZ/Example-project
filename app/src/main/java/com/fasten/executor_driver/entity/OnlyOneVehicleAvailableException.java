package com.fasten.executor_driver.entity;

/**
 * Исключение о доступности только одного ТС.
 */
public class OnlyOneVehicleAvailableException extends Exception {

  public OnlyOneVehicleAvailableException() {
    super();
  }
}
