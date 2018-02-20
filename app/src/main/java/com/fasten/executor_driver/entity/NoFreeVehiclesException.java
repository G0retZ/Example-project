package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Исключение об отстустствии свободных ТС.
 */
public class NoFreeVehiclesException extends IOException {

  public NoFreeVehiclesException() {
    super();
  }
}
