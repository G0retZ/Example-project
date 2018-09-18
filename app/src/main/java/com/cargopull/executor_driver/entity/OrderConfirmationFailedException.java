package com.cargopull.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Ошибка подтверждения/бронирования или отказа от заказ.
 */
public class OrderConfirmationFailedException extends Exception {

  public OrderConfirmationFailedException(@NonNull String message) {
    super(message);
  }
}
