package com.cargopull.executor_driver.presentation.preorder;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна заказа.
 */
public interface PreOrderViewModel extends ViewModel<PreOrderViewActions> {

  /**
   * Сообщает, что сообщение об прочитано.
   */
  void preOrderConsumed();
}
