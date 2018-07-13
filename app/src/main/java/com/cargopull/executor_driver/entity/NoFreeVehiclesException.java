package com.cargopull.executor_driver.entity;

/**
 * Исключение об отстустствии свободных ТС.
 */
public class NoFreeVehiclesException extends Exception {

  public NoFreeVehiclesException() {
    super();
  }
}
