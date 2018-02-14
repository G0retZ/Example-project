package com.fasten.executor_driver.gateway;

import java.io.IOException;

/**
 * Исключение об ошибке преобразования данных.
 */
class DataMappingException extends IOException {

  @SuppressWarnings("unused")
  DataMappingException() {
    super();
  }

  DataMappingException(String message) {
    super(message);
  }

  @SuppressWarnings("SameParameterValue")
  DataMappingException(String message, Throwable cause) {
    super(message, cause);
  }

  @SuppressWarnings("unused")
  DataMappingException(Throwable cause) {
    super(cause);
  }
}
