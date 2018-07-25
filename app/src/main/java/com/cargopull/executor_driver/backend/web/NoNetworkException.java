package com.cargopull.executor_driver.backend.web;

import java.io.IOException;

/**
 * Исключение об отсутствии сети.
 */

public class NoNetworkException extends IOException {

  public NoNetworkException() {
    super();
  }
}
