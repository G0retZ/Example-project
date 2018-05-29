package com.fasten.executor_driver.presentation.movingtoclient;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна движения к клиенту.
 */
public interface MovingToClientViewModel extends ViewModel<MovingToClientViewActions> {

  /**
   * Сообщает о прибытии в место встречи.
   */
  void reportArrival();
}
