package com.cargopull.executor_driver.presentation.orderconfirmation;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна заказа.
 */
public interface OrderConfirmationViewModel extends ViewModel<OrderConfirmationViewActions> {

  /**
   * Принимает заказ.
   */
  void acceptOrder();

  /**
   * Отказывается от заказа.
   */
  void declineOrder();

  /**
   * Сообщает, что таймер закончился.
   */
  void counterTimeOut();
}
