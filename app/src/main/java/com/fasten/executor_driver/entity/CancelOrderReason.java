package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность причины отказа от заказа. Immutable.
 * Создается через конструктор с не нулевыми полями.
 */
public class CancelOrderReason {

  private final int id;
  @NonNull
  private final String name;

  public CancelOrderReason(int id, @NonNull String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  @NonNull
  public String getName() {
    return name;
  }
}
