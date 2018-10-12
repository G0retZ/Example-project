package com.cargopull.executor_driver.presentation.order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние неактуальности заказа.
 */
final class OrderViewStateExpired implements ViewState<OrderViewActions> {

  @Nullable
  private final ViewState<OrderViewActions> parentViewState;
  @NonNull
  private final String message;

  OrderViewStateExpired(@Nullable ViewState<OrderViewActions> parentViewState,
      @NonNull String message) {
    this.parentViewState = parentViewState;
    this.message = message;
  }

  @Override
  public void apply(@NonNull OrderViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showOrderExpiredMessage(message);
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

    OrderViewStateExpired that = (OrderViewStateExpired) o;

    if (parentViewState != null ? !parentViewState.equals(that.parentViewState)
        : that.parentViewState != null) {
      return false;
    }
    return message.equals(that.message);
  }

  @Override
  public int hashCode() {
    int result = parentViewState != null ? parentViewState.hashCode() : 0;
    result = 31 * result + message.hashCode();
    return result;
  }
}
