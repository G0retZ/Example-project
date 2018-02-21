package com.fasten.executor_driver.entity;

/**
 * Исключение об отстустствии доступных для изменения опций ТС.
 */
public class NoVehicleOptionsAvailableException extends Exception {

  public NoVehicleOptionsAvailableException() {
    super();
  }
}
