package com.fasten.executor_driver.presentation.executorstate;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна заставки.
 */
public interface ExecutorStateViewModel extends ViewModel<ExecutorStateViewActions> {

  /**
   * Запрашивает инициализацию приложения.
   */
  void initializeExecutorState();
}
