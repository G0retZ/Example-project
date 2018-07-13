package com.cargopull.executor_driver.presentation.balance;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания загрузки списка ТС.
 */
final class BalanceViewStatePending implements ViewState<BalanceViewActions> {

  @Nullable
  private final ViewState<BalanceViewActions> parentViewState;

  BalanceViewStatePending(@Nullable ViewState<BalanceViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull BalanceViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showBalancePending(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BalanceViewStatePending that = (BalanceViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
