package com.fasten.executor_driver.presentation.offer;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна заказа.
 */
interface OfferViewModel extends ViewModel<OfferViewActions> {

  /**
   * Принимает заказ.
   */
  void acceptOffer();

  /**
   * Отказывается от заказа.
   */
  void declineOffer();
}
