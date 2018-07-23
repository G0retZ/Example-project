package com.cargopull.executor_driver.presentation.confirmorderpayment;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна подтверждения оплаты заказа.
 */
interface ConfirmOrderPaymentViewModel extends ViewModel<ConfirmOrderPaymentViewActions> {

  /**
   * Запрашивает подтверждение оплаты заказа.
   */
  void confirmPayment();
}
