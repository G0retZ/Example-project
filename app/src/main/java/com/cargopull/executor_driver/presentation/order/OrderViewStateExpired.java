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
  @NonNull
  private final Runnable consumeAction;

  OrderViewStateExpired(@Nullable ViewState<OrderViewActions> parentViewState,
      @NonNull String message,
      @NonNull Runnable consumeAction) {
    this.parentViewState = parentViewState;
    this.message = message;
    this.consumeAction = consumeAction;
  }

  @Override
  public void apply(@NonNull OrderViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showPersistentDialog(message, consumeAction);
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
