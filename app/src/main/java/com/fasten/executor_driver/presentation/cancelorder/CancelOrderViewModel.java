package com.fasten.executor_driver.presentation.cancelorder;

import com.fasten.executor_driver.entity.CancelOrderReason;
import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна выбора причины отказа.
 */
interface CancelOrderViewModel extends ViewModel<CancelOrderViewActions> {

  /**
   * Передает выбранную причину отказа.
   *
   * @param cancelOrderReason - эелемент списка ТС
   */
  void selectItem(CancelOrderReason cancelOrderReason);
}
