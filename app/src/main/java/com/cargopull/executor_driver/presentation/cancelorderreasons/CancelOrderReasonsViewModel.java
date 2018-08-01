package com.cargopull.executor_driver.presentation.cancelorderreasons;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel списка причин для отказа.
 */
public interface CancelOrderReasonsViewModel extends ViewModel<Runnable> {

  /**
   * Запрашивает подписку на список причин для отказа со сбросом кеша.
   */
  void initializeCancelOrderReasons();
}
