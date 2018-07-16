package com.cargopull.executor_driver.presentation.onlinebutton;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel кнопки выхода на линию.
 */
public interface OnlineButtonViewModel extends ViewModel<OnlineButtonViewActions> {

  /**
   * Запрашивает выход на линию.
   */
  void goOnline();
}
