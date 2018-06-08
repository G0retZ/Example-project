package com.fasten.executor_driver.presentation.balance;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при получении списка причин отказа или отправке причины отказа.
 */
final class BalanceViewStateError implements ViewState<BalanceViewActions> {

  @Nullable
  private final ViewState<BalanceViewActions> parentViewState;

  BalanceViewStateError(@Nullable ViewState<BalanceViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull BalanceViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showBalanceErrorMessage(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BalanceViewStateError that = (BalanceViewStateError) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
