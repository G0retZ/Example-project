package com.cargopull.executor_driver.entity;

/**
 * Ошибка: заказ был отменен.
 */
public class OrderCancelledException extends Exception {

  public OrderCancelledException() {
    super();
  }

  public OrderCancelledException(String message) {
    super(message);
  }
}
