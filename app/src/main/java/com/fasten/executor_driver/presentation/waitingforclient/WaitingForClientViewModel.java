package com.fasten.executor_driver.presentation.waitingforclient;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна ожидания клиента.
 */
interface WaitingForClientViewModel extends ViewModel<WaitingForClientViewActions> {

  /**
   * Запрашивает звонок клиенту.
   */
  void callToClient();

  /**
   * Сообщает о начале погрузки.
   */
  void startLoading();
}
