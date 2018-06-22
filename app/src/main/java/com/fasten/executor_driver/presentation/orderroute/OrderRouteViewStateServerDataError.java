package com.fasten.executor_driver.presentation.orderroute;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки  списка точек маршрута заказа.
 */
public final class OrderRouteViewStateServerDataError implements ViewState<OrderRouteViewActions> {

  @Nullable
  private final ViewState<OrderRouteViewActions> parentViewState;

  OrderRouteViewStateServerDataError(@Nullable ViewState<OrderRouteViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull OrderRouteViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showOrderRouteServerDataError();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderRouteViewStateServerDataError that = (OrderRouteViewStateServerDataError) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
