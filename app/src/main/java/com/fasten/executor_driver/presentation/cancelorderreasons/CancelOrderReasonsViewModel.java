package com.fasten.executor_driver.presentation.cancelorderreasons;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel списка причин для отказа.
 */
public interface CancelOrderReasonsViewModel extends ViewModel<CancelOrderReasonsViewActions> {

  /**
   * Запрашивает подписку на список причин для отказа со сбросом кеша.
   */
  void initializeCancelOrderReasons();
}
