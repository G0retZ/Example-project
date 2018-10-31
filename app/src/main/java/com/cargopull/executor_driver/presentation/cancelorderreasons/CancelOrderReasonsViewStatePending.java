package com.cargopull.executor_driver.presentation.cancelorderreasons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания загрузки списка ТС.
 */
final class CancelOrderReasonsViewStatePending implements ViewState<CancelOrderReasonsViewActions> {

  @Nullable
  private final ViewState<CancelOrderReasonsViewActions> parentViewState;

  CancelOrderReasonsViewStatePending(
      @Nullable ViewState<CancelOrderReasonsViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull CancelOrderReasonsViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showCancelOrderReasons(parentViewState != null);
    stateActions.showCancelOrderReasonsPending(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CancelOrderReasonsViewStatePending that = (CancelOrderReasonsViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
