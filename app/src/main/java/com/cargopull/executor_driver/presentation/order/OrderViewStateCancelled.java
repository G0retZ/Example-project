package com.cargopull.executor_driver.presentation.order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние отмененного заказа.
 */
final class OrderViewStateCancelled implements ViewState<OrderViewActions> {

  @Nullable
  private final ViewState<OrderViewActions> parentViewState;
  @NonNull
  private final Runnable consumeAction;

  OrderViewStateCancelled(@Nullable ViewState<OrderViewActions> parentViewState,
      @NonNull Runnable consumeAction) {
    this.parentViewState = parentViewState;
    this.consumeAction = consumeAction;
  }

  @Override
  public void apply(@NonNull OrderViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showPersistentDialog(R.string.order_cancelled, consumeAction);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderViewStateCancelled that = (OrderViewStateCancelled) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
