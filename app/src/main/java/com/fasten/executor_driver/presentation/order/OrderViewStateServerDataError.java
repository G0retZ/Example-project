package com.fasten.executor_driver.presentation.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки сети вида заказа.
 */
final class OrderViewStateServerDataError implements ViewState<OrderViewActions> {

  @Nullable
  private final ViewState<OrderViewActions> parentViewState;

  OrderViewStateServerDataError(@Nullable ViewState<OrderViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull OrderViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showOrderPending(false);
    stateActions.showOrderServerDataError();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderViewStateServerDataError that = (OrderViewStateServerDataError) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
