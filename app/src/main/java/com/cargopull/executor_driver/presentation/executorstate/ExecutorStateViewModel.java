package com.cargopull.executor_driver.presentation.executorstate;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel статусов исполнителя.
 */
public interface ExecutorStateViewModel extends ViewModel<ExecutorStateViewActions> {

  /**
   * Запрашивает подписку на статус исполнителя со сбросом.
   */
  void initializeExecutorState();
}
