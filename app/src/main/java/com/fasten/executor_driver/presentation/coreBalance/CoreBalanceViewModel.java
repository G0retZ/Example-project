package com.fasten.executor_driver.presentation.coreBalance;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel основы баланса.
 */
interface CoreBalanceViewModel extends ViewModel<CoreBalanceViewActions> {

  /**
   * Запрашивает подписку на основу баланса со сбросом кеша или без.
   *
   * @param reset - сбросить ли кеш?
   */
  void initializeExecutorBalance(boolean reset);
}
