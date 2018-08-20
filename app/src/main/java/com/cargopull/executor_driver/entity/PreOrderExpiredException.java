package com.cargopull.executor_driver.entity;

/**
 * Ошибка: предзаказ уже забронирован.
 */
public class PreOrderExpiredException extends Exception {

  public PreOrderExpiredException() {
    super();
  }

  public PreOrderExpiredException(String message) {
    super(message);
  }
}
