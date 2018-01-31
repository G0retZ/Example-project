package com.fasten.executor_driver.backend.web;

import java.io.IOException;

/**
 * Исключение об отсутствии сети.
 */

public class NoNetworkException extends IOException {

  public NoNetworkException() {
    super();
  }

  @SuppressWarnings("unused")
  public NoNetworkException(String message) {
    super(message);
  }

  @SuppressWarnings("unused")
  public NoNetworkException(String message, Throwable cause) {
    super(message, cause);
  }

  @SuppressWarnings("unused")
  public NoNetworkException(Throwable cause) {
    super(cause);
  }
}
