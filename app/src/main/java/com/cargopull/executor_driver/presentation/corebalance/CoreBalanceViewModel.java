package com.cargopull.executor_driver.presentation.corebalance;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel основы баланса.
 */
public interface CoreBalanceViewModel extends ViewModel<Runnable> {

  /**
   * Запрашивает подписку на основу баланса со сбросом кеша.
   */
  void initializeExecutorBalance();
}
