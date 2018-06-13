package com.fasten.executor_driver.presentation.balance;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна баланса.
 */
public interface BalanceViewModel extends ViewModel<BalanceViewActions> {

  /**
   * Пополняет счет.
   */
  void replenishAccount();
}
