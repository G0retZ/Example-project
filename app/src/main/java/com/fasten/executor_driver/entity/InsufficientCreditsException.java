package com.fasten.executor_driver.entity;

import java.io.IOException;

/**
 * Ошибка: недостаточно средств.
 */
public class InsufficientCreditsException extends IOException {

  @SuppressWarnings("SameParameterValue")
  public InsufficientCreditsException(String message) {
    super(message);
  }
}
