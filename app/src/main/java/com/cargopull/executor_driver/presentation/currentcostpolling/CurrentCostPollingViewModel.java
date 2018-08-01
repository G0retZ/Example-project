package com.cargopull.executor_driver.presentation.currentcostpolling;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel статусов исполнителя.
 */
public interface CurrentCostPollingViewModel extends ViewModel<Runnable> {

  /**
   * Запрашивает подписку на статус исполнителя со сбросом.
   */
  void initializeCurrentCostPolling();
}
