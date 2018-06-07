package com.fasten.executor_driver.presentation.calltoclient;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна звонка клиенту.
 */
public interface CallToClientViewModel extends ViewModel<CallToClientViewActions> {

  /**
   * Запрашивает звонок клиенту.
   */
  void callToClient();
}
