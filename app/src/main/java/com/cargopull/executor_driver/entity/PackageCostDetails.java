package com.cargopull.executor_driver.entity;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.Pair;
import java.util.Collections;
import java.util.List;

/**
 * Неизменная бизнес сущность детализации стоимости пакета заказа.
 */
public class PackageCostDetails {

  private final long packageTime;
  private final int packageDistance;
  private final long packageCost;
  private final long serviceCost;
  @NonNull
  private final List<Pair<String, Long>> optionCosts;

  public PackageCostDetails(long packageTime, int packageDistance, long packageCost,
      long serviceCost, @NonNull List<Pair<String, Long>> optionCosts) {
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
  public List<Pair<String, Long>> getOptionCosts() {
    return optionCosts;
  }
}
