package com.fasten.executor_driver.presentation.order;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки доступности зака вида заказа.
 */
final class OrderViewStateUnavailableError implements ViewState<OrderViewActions> {

  @Nullable
  private final ViewState<OrderViewActions> parentViewState;

  OrderViewStateUnavailableError(@Nullable ViewState<OrderViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull OrderViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showOrderPending(false);
    stateActions.showOrderAvailabilityError();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderViewStateUnavailableError that = (OrderViewStateUnavailableError) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
