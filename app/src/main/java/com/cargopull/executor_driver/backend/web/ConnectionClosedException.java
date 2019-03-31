package com.cargopull.executor_driver.backend.web;

import java.io.IOException;

/**
 * Исключение о закрытом соединении.
 */

public class ConnectionClosedException extends IOException {

  public ConnectionClosedException() {
    super();
  }
}
