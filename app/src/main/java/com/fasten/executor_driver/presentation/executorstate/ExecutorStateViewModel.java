package com.fasten.executor_driver.presentation.executorstate;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна заставки.
 */
public interface ExecutorStateViewModel extends ViewModel<ExecutorStateViewActions> {

  /**
   * Запрашивает инициализацию приложения со сбросом кеша или без.
   *
   * @param reset - сбросить ли кеш?
   */
  void initializeExecutorState(boolean reset);
}
