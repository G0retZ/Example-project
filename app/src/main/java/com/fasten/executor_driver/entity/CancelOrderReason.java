package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность причины отказа от заказа. Immutable.
 * Создается через конструктор с не нулевыми полями.
 */
public class CancelOrderReason {

  @NonNull
  private final String id;
  @NonNull
  private final String name;

  CancelOrderReason(@NonNull String id, @NonNull String name) {
    this.id = id;
    this.name = name;
  }

  @NonNull
  public String getId() {
    return id;
  }

  @NonNull
  public String getName() {
    return name;
  }
}
