package com.fasten.executor_driver.presentation.executorstate;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel статусов исполнителя.
 */
public interface ExecutorStateViewModel extends ViewModel<ExecutorStateViewActions> {

  /**
   * Запрашивает подписку на статус исполнителя со сбросом кеша или без.
   *
   * @param reset - сбросить ли кеш?
   */
  void initializeExecutorState(boolean reset);
}
