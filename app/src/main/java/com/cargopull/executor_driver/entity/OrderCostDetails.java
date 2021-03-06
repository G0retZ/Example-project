package com.cargopull.executor_driver.entity;

import androidx.annotation.Nullable;

/**
 * Неизменная бизнес сущность детализации стоимости заказа.
 */
public class OrderCostDetails {

  private final long orderCost;
  @Nullable
  private final PackageCostDetails estimatedCost;
  @Nullable
  private final PackageCostDetails overPackageCost;
  @Nullable
  private final PackageCostDetails overPackageTariff;

  public OrderCostDetails(long orderCost,
      @Nullable PackageCostDetails estimatedCost,
      @Nullable PackageCostDetails overPackageCost,
      @Nullable PackageCostDetails overPackageTariff) {
    this.orderCost = orderCost;
    this.estimatedCost = estimatedCost;
    this.overPackageCost = overPackageCost;
    this.overPackageTariff = overPackageTariff;
  }

  public long getOrderCost() {
    return orderCost;
  }

  @Nullable
  public PackageCostDetails getEstimatedCost() {
    return estimatedCost;
  }

  @Nullable
  public PackageCostDetails getOverPackageCost() {
    return overPackageCost;
  }

  @Nullable
  public PackageCostDetails getOverPackageTariff() {
    return overPackageTariff;
  }
}
