package com.cargopull.executor_driver.presentation.ordershistoryheader;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна истории заказов.
 */
public interface OrdersHistoryHeaderViewModel extends ViewModel<OrdersHistoryHeaderViewActions> {

  /**
   * Повторяет загрузку данных для текущего смещения.
   */
  void retry();
}
