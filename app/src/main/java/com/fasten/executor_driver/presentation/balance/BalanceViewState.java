package com.fasten.executor_driver.presentation.balance;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorBalance;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние вида баланса.
 */
final class BalanceViewState implements ViewState<BalanceViewActions> {

  @NonNull
  private final ExecutorBalance executorBalance;

  BalanceViewState(@NonNull ExecutorBalance executorBalance) {
    this.executorBalance = executorBalance;
  }

  @Override
  public void apply(@NonNull BalanceViewActions balanceViewActions) {
    balanceViewActions.showMainAccountAmount(executorBalance.getMainAccount());
    balanceViewActions.showBonusAccountAmount(executorBalance.getBonusAccount());
    balanceViewActions.showBalancePending(false);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BalanceViewState that = (BalanceViewState) o;

    return executorBalance.equals(that.executorBalance);
  }

  @Override
  public int hashCode() {
    return executorBalance.hashCode();
  }
}
