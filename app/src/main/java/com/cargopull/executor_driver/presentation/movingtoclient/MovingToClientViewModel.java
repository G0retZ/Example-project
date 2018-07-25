package com.cargopull.executor_driver.presentation.movingtoclient;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна движения к клиенту.
 */
public interface MovingToClientViewModel extends ViewModel<MovingToClientViewActions> {

  /**
   * Запрашивает звонок клиенту.
   */
  void callToClient();

  /**
   * Сообщает о прибытии в место встречи.
   */
  void reportArrival();
}
