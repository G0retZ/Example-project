package com.cargopull.executor_driver.presentation.confirmorderpayment;

/**
 * Действия для смены состояния вида окна звонка клиенту.
 */
public interface ConfirmOrderPaymentViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void ConfirmOrderPaymentPending(boolean pending);
}
