package com.cargopull.executor_driver.entity;

/**
 * Ошибка: водитель заблокирован.
 */
public class DriverBlockedException extends Exception {

  public DriverBlockedException() {
    super();
  }

  public DriverBlockedException(String message) {
    super(message);
  }
}
