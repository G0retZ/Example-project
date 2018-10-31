package com.cargopull.executor_driver.presentation.ordecostdetails;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида заказа.
 */
final class OrderCostDetailsViewStateIdle implements ViewState<OrderCostDetailsViewActions> {

  @NonNull
  private final OrderCostDetailsItem orderCostDetailsItem;

  OrderCostDetailsViewStateIdle(@NonNull OrderCostDetailsItem orderCostDetailsItem) {
    this.orderCostDetailsItem = orderCostDetailsItem;
  }

  @Override
  public void apply(@NonNull OrderCostDetailsViewActions stateActions) {
    stateActions.showOrderCostDetailsPending(false);
    stateActions.showOrderTotalCost(orderCostDetailsItem.getTotalCost());
    stateActions.showEstimatedOrderPackage(orderCostDetailsItem.getEstimatedPackage() != null);
    if (orderCostDetailsItem.getEstimatedPackage() != null) {
      stateActions.showEstimatedOrderCost(orderCostDetailsItem.getEstimatedPackage().getCost());
      stateActions.showEstimatedOrderServiceCost(
          orderCostDetailsItem.getEstimatedPackage().getServiceCost());
      stateActions.showEstimatedOrderTime(orderCostDetailsItem.getEstimatedPackage().getTime());
      stateActions.showEstimatedOrderDistance(
          orderCostDetailsItem.getEstimatedPackage().getDistance());
      stateActions.showEstimatedOrderOptionsCosts(
          orderCostDetailsItem.getEstimatedPackage().getOptionsCosts());
    }
    stateActions.showOverPackage(orderCostDetailsItem.getOverPackage() != null);
    if (orderCostDetailsItem.getOverPackage() != null) {
      stateActions.showOverPackageCost(orderCostDetailsItem.getOverPackage().getCost());
      stateActions.showOverPackageServiceCost(
          orderCostDetailsItem.getOverPackage().getServiceCost());
      stateActions.showOverPackageTime(orderCostDetailsItem.getOverPackage().getTime());
      stateActions.showOverPackageOptionsCosts(
          orderCostDetailsItem.getOverPackage().getOptionsCosts());
    }
    stateActions.showOverPackageTariff(orderCostDetailsItem.getOverPackageTariff() != null);
    if (orderCostDetailsItem.getOverPackageTariff() != null) {
      stateActions.showOverPackageTariffCost(orderCostDetailsItem.getOverPackageTariff().getCost());
      stateActions.showOverPackageServiceTariff(
          orderCostDetailsItem.getOverPackageTariff().getServiceCost());
      stateActions.showOverPackageOptionsTariffs(
          orderCostDetailsItem.getOverPackageTariff().getOptionsCosts());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderCostDetailsViewStateIdle that = (OrderCostDetailsViewStateIdle) o;

    return orderCostDetailsItem.equals(that.orderCostDetailsItem);
  }

  @Override
  public int hashCode() {
    return orderCostDetailsItem.hashCode();
  }
}
