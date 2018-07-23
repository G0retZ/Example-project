package com.cargopull.executor_driver.presentation.ordecostdetails;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.PackageCostDetails;
import com.cargopull.executor_driver.utils.Pair;
import java.util.List;
import java.util.Locale;

/**
 * Модель для отображения детального расчета пакета заказа. Тестируемое форматирование.
 */
class PackageCostDetailsItem {

  @NonNull
  private final PackageCostDetails packageCostDetails;

  PackageCostDetailsItem(@NonNull PackageCostDetails packageCostDetails) {
    this.packageCostDetails = packageCostDetails;
  }

  public long getCost() {
    return packageCostDetails.getPackageCost();
  }

  public long getTime() {
    return packageCostDetails.getPackageTime();
  }

  public String getDistance() {
    return String
        .format(Locale.getDefault(), "%.2f", packageCostDetails.getPackageDistance() / 1000d);
  }

  public long getServiceCost() {
    return packageCostDetails.getServiceCost();
  }

  public List<Pair<String, Integer>> getOptionsCosts() {
    return packageCostDetails.getOptionCosts();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PackageCostDetailsItem that = (PackageCostDetailsItem) o;

    return packageCostDetails.equals(that.packageCostDetails);
  }

  @Override
  public int hashCode() {
    return packageCostDetails.hashCode();
  }
}
