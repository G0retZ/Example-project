package com.fasten.executor_driver.presentation.orderconfirmation;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна заказа.
 */
public interface OrderConfirmationViewModel extends ViewModel<OrderConfirmationViewActions> {

  /**
   * Отказывается от заказа.
   */
  void cancelOrder();
}
