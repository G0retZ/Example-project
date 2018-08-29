package com.cargopull.executor_driver.presentation.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние неактуальности заказа.
 */
final class OrderViewStateExpired implements ViewState<OrderViewActions> {

  @Nullable
  private final ViewState<OrderViewActions> parentViewState;

  OrderViewStateExpired(@Nullable ViewState<OrderViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull OrderViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showOrderExpired(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderViewStateExpired that = (OrderViewStateExpired) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
