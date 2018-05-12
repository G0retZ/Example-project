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
  void acceptOffer();

  /**
   * Отказывается от заказа.
   */
  void declineOffer();

  /**
   * Сообщает, что таймер закончился.
   */
  void counterTimeOut();
}
