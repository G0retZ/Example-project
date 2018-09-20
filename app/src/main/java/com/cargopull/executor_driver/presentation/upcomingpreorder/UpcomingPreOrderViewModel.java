package com.cargopull.executor_driver.presentation.upcomingpreorder;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна предстоящего предзаказа.
 */
public interface UpcomingPreOrderViewModel extends ViewModel<UpcomingPreOrderViewActions> {

  /**
   * Сообщает, что сообщение об прочитано.
   */
  void upcomingPreOrderConsumed();
}
