package com.fasten.executor_driver.presentation.driverorderconfirmation;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна заказа.
 */
public interface DriverOrderConfirmationViewModel extends
    ViewModel<DriverOrderConfirmationViewActions> {

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
