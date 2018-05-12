package com.fasten.executor_driver.presentation.clientorderconfirmation;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна заказа.
 */
public interface ClientOrderConfirmationViewModel extends
    ViewModel<ClientOrderConfirmationViewActions> {

  /**
   * Отказывается от заказа.
   */
  void cancelOrder();
}
