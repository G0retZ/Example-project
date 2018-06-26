package com.fasten.executor_driver.presentation.currentcostpolling;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel статусов исполнителя.
 */
public interface CurrentCostPollingViewModel extends ViewModel<CurrentCostPollingViewActions> {

  /**
   * Запрашивает подписку на статус исполнителя со сбросом.
   */
  void initializeCurrentCostPolling();
}
