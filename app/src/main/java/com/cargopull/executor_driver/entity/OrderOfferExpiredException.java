package com.cargopull.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Ошибка: заказ более не актуален.
 */
public class OrderOfferExpiredException extends Exception {

  public OrderOfferExpiredException(@NonNull String message) {
    super(message);
  }
}
