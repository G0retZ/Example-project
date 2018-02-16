package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Исключение об отстустствии доступных для изменения опций ТС.
 */
public class NoVehicleOptionsAvailableException extends IOException {

  public NoVehicleOptionsAvailableException() {
    super();
  }
}
