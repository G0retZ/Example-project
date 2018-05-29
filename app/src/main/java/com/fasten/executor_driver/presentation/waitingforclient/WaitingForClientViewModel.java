package com.fasten.executor_driver.presentation.waitingforclient;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна ожидания клиента.
 */
public interface WaitingForClientViewModel extends ViewModel<WaitingForClientViewActions> {

  /**
   * Сообщает о начале погрузки.
   */
  void startLoading();
}
