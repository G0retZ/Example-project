package com.cargopull.executor_driver.backend.stomp;

public enum Command {
  // Client commands
  CONNECT,
  SEND,
  SUBSCRIBE,
  UNSUBSCRIBE,
  ACK,
  DISCONNECT,
  // Server commands
  CONNECTED,
  MESSAGE,
  HEARTBEAT,
  ERROR
}
