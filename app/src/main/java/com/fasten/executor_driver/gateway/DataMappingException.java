package com.fasten.executor_driver.gateway;

import java.io.IOException;

/**
 * Исключение об ошибке преобразования данных.
 */
public class DataMappingException extends IOException {

  public DataMappingException() {
    super();
  }

  DataMappingException(String message) {
    super(message);
  }

  @SuppressWarnings("SameParameterValue")
  DataMappingException(String message, Throwable cause) {
    super(message, cause);
  }
}
