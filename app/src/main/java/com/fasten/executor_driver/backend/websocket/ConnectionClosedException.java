package com.fasten.executor_driver.backend.websocket;

import java.io.IOException;

/**
 * Исключение о закрытом соединении.
 */

public class ConnectionClosedException extends IOException {

  public ConnectionClosedException() {
    super();
  }
}
