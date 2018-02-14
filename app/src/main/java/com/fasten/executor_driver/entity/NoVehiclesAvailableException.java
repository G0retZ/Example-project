package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Исключение об отстустствии доступных ТС.
 */
public class NoVehiclesAvailableException extends IOException {

  public NoVehiclesAvailableException() {
    super();
  }
}
