package com.cargopull.executor_driver.presentation.smsbutton;

import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel кнопки отправки СМС с таймером.
 */
public interface SmsButtonViewModel extends ViewModel<FragmentViewActions> {

  /**
   * Запрашивает отправку СМС с кодом.
   */
  void sendMeSms();
}
