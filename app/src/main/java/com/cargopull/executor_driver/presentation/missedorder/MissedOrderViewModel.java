package com.cargopull.executor_driver.presentation.missedorder;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel сообщений об упущенных заказах.
 */
public interface MissedOrderViewModel extends ViewModel<MissedOrderViewActions> {

  /**
   * Запрашивает подписку на сообщения об упущенных заказах.
   */
  void initializeMissedOrderMessages();
}
