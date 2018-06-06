package com.fasten.executor_driver.presentation.cancelorder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при получении списка причин отказа или отправке причины отказа.
 */
final class CancelOrderViewStateError implements ViewState<CancelOrderViewActions> {

  @Nullable
  private final ViewState<CancelOrderViewActions> parentViewState;

  CancelOrderViewStateError(@Nullable ViewState<CancelOrderViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull CancelOrderViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showCancelOrderReasons(parentViewState != null);
    stateActions.showCancelOrderErrorMessage(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CancelOrderViewStateError that = (CancelOrderViewStateError) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
