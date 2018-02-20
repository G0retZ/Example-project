package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Ошибка: недостаточно средств.
 */
public class InsufficientCreditsException extends IOException {

  public InsufficientCreditsException() {
    super();
  }

  @SuppressWarnings("SameParameterValue")
  public InsufficientCreditsException(String message) {
    super(message);
  }
}
