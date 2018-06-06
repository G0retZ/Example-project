package com.fasten.executor_driver.presentation.cancelorder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания загрузки списка ТС.
 */
final class CancelOrderViewStatePending implements ViewState<CancelOrderViewActions> {

  @Nullable
  private final ViewState<CancelOrderViewActions> parentViewState;

  CancelOrderViewStatePending(@Nullable ViewState<CancelOrderViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull CancelOrderViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showCancelOrderReasons(parentViewState != null);
    stateActions.showCancelOrderPending(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CancelOrderViewStatePending that = (CancelOrderViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
