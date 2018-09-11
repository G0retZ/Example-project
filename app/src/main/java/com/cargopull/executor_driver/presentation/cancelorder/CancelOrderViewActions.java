package com.cargopull.executor_driver.presentation.cancelorder;

/**
 * Действия для смены состояния вида окна списка ТС исполнителя.
 */
public interface CancelOrderViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showCancelOrderPending(boolean pending);
}
