package com.cargopull.executor_driver.entity;

import android.support.annotation.NonNull;
import java.util.Collections;
import java.util.List;

/**
 * Неизменная бизнес сущность детализации стоимости пакета заказа.
 */
class PackageCostDetails {

  @NonNull
  public final List<PackageOptionCost> optionCosts;
  private final long packageTime;
  private final int packageDistance;
  private final long packageCost;
  private final long serviceCost;

  PackageCostDetails(long packageTime, int packageDistance, long packageCost,
      long serviceCost, @NonNull List<PackageOptionCost> optionCosts) {
    this.packageTime = packageTime;
    this.packageDistance = packageDistance;
    this.packageCost = packageCost;
    this.serviceCost = serviceCost;
    this.optionCosts = Collections.unmodifiableList(optionCosts);
  }

  public long getPackageTime() {
    return packageTime;
  }

  public int getPackageDistance() {
    return packageDistance;
  }

  public long getPackageCost() {
    return packageCost;
  }

  public long getServiceCost() {
    return serviceCost;
  }

  @NonNull
  public List<PackageOptionCost> getOptionCosts() {
    return optionCosts;
  }
}
