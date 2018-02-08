package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Ошибка: недостаточно средств.
 */

public class InsufficientCreditsException extends IOException {

  public InsufficientCreditsException() {
    super();
  }

  @SuppressWarnings("unused")
  public InsufficientCreditsException(String message) {
    super(message);
  }

  @SuppressWarnings("unused")
  public InsufficientCreditsException(String message, Throwable cause) {
    super(message, cause);
  }

  @SuppressWarnings("unused")
  public InsufficientCreditsException(Throwable cause) {
    super(cause);
  }
}
