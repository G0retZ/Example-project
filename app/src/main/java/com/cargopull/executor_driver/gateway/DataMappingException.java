package com.cargopull.executor_driver.gateway;

import java.io.IOException;

/**
 * Исключение об ошибке преобразования данных.
 */
public class DataMappingException extends IOException {

  public DataMappingException() {
    super();
  }

  public DataMappingException(Throwable cause) {
    super(cause);
  }

  DataMappingException(String message) {
    super(message);
  }

  DataMappingException(String message, Throwable cause) {
    super(message, cause);
  }
}
