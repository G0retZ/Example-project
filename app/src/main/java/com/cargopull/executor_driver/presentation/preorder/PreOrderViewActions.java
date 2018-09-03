package com.cargopull.executor_driver.presentation.preorder;

/**
 * Действия для смены состояния вида окна предзаказа.
 */
public interface PreOrderViewActions {

  /**
   * показать сообщение о том, что доступен предзаказ.
   *
   * @param show - показать или нет?
   */
  void showPreOrderAvailable(boolean show);
}
