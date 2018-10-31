package com.cargopull.executor_driver.presentation.orderroute;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания списка точек маршрута заказа.
 */
public final class OrderRouteViewStatePending implements ViewState<OrderRouteViewActions> {

  @Nullable
  private final ViewState<OrderRouteViewActions> parentViewState;

  OrderRouteViewStatePending(@Nullable ViewState<OrderRouteViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull OrderRouteViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showOrderRoutePending(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderRouteViewStatePending that = (OrderRouteViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
