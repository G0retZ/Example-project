package com.cargopull.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Неизменная бизнес сущность стоимости опции заказа.
 */
class PackageOptionCost {

  @NonNull
  private final String name;
  private final int cost;

  PackageOptionCost(@NonNull String name, int cost) {
    this.name = name;
    this.cost = cost;
  }

  @NonNull
  public String getName() {
    return name;
  }

  public int getCost() {
    return cost;
  }
}
