package com.cargopull.executor_driver.presentation.ordecostdetails;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class OrderCostDetailsViewStatePending implements ViewState<OrderCostDetailsViewActions> {

  @Nullable
  private final ViewState<OrderCostDetailsViewActions> parentViewState;

  OrderCostDetailsViewStatePending(
      @Nullable ViewState<OrderCostDetailsViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull OrderCostDetailsViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showOrderCostDetailsPending(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderCostDetailsViewStatePending that = (OrderCostDetailsViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
