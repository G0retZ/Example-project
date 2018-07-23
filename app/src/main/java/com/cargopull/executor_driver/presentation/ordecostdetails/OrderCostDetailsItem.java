package com.cargopull.executor_driver.presentation.ordecostdetails;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.OrderCostDetails;

/**
 * Модель для отображения детального расчета заказа. Тестируемое форматирование.
 */
class OrderCostDetailsItem {

  @NonNull
  private final OrderCostDetails orderCostDetails;
  @Nullable
  private final PackageCostDetailsItem estimatedPackage;
  @Nullable
  private final PackageCostDetailsItem overPackage;
  @Nullable
  private final PackageCostDetailsItem overPackageTariff;

  OrderCostDetailsItem(@NonNull OrderCostDetails orderCostDetails) {
    this.orderCostDetails = orderCostDetails;
    estimatedPackage = orderCostDetails.getEstimatedCost() == null ? null
        : new PackageCostDetailsItem(orderCostDetails.getEstimatedCost());
    overPackage = orderCostDetails.getOverPackageCost() == null ? null
        : new PackageCostDetailsItem(orderCostDetails.getOverPackageCost());
    overPackageTariff = orderCostDetails.getOverPackageTariff() == null ? null
        : new PackageCostDetailsItem(orderCostDetails.getOverPackageTariff());
  }

  public long getTotalCost() {
    return orderCostDetails.getOrderCost();
  }

  @Nullable
  public PackageCostDetailsItem getEstimatedPackage() {
    return estimatedPackage;
  }

  @Nullable
  public PackageCostDetailsItem getOverPackage() {
    return overPackage;
  }

  @Nullable
  public PackageCostDetailsItem getOverPackageTariff() {
    return overPackageTariff;
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderCostDetailsItem that = (OrderCostDetailsItem) o;

    if (!orderCostDetails.equals(that.orderCostDetails)) {
      return false;
    }
    if (estimatedPackage != null ? !estimatedPackage.equals(that.estimatedPackage)
        : that.estimatedPackage != null) {
      return false;
    }
    if (overPackage != null ? !overPackage.equals(that.overPackage) : that.overPackage != null) {
      return false;
    }
    return overPackageTariff != null ? overPackageTariff.equals(that.overPackageTariff)
        : that.overPackageTariff == null;
  }

  @Override
  public int hashCode() {
    int result = orderCostDetails.hashCode();
    result = 31 * result + (estimatedPackage != null ? estimatedPackage.hashCode() : 0);
    result = 31 * result + (overPackage != null ? overPackage.hashCode() : 0);
    result = 31 * result + (overPackageTariff != null ? overPackageTariff.hashCode() : 0);
    return result;
  }
}
