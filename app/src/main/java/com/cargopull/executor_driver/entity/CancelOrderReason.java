package com.cargopull.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность причины отказа от заказа. Immutable.
 * Создается через конструктор с не нулевыми полями.
 */
public class CancelOrderReason {

  private final int id;
  @NonNull
  private final String name;
  @NonNull
  private final String unusedName;

  public CancelOrderReason(int id, @NonNull String name, @NonNull String unusedName) {
    this.id = id;
    this.name = name;
    this.unusedName = unusedName;
  }

  public int getId() {
    return id;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
  public String getUnusedName() {
    return unusedName;
  }

  @Override
  public String toString() {
    return "CancelOrderReason{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", unusedName='" + unusedName + '\'' +
        '}';
  }
}
