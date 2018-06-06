package com.fasten.executor_driver.presentation.cancelorderreasons;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel списка причин для отказа.
 */
interface CancelOrderReasonsViewModel extends ViewModel<CancelOrderReasonsViewActions> {

  /**
   * Запрашивает подписку на список причин для отказа со сбросом кеша или без.
   *
   * @param reset - сбросить ли кеш?
   */
  void initializeCancelOrderReasons(boolean reset);
}
