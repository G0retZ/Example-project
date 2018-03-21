package com.fasten.executor_driver.backend.websocket;

import java.io.IOException;

/**
 * Исключение о закрытом соединении.
 */

@SuppressWarnings("unused")
class ConnectionClosedException extends IOException {

  ConnectionClosedException() {
    super();
  }
}
