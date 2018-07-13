package com.cargopull.executor_driver.presentation.balance;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна баланса.
 */
public interface BalanceViewModel extends ViewModel<BalanceViewActions> {

  /**
   * Пополняет счет.
   */
  void replenishAccount();
}
