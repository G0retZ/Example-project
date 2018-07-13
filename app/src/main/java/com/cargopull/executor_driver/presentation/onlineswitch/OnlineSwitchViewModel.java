package com.cargopull.executor_driver.presentation.onlineswitch;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel переключателя онлайна.
 */
public interface OnlineSwitchViewModel extends ViewModel<OnlineSwitchViewActions> {

  /**
   * Запрашивает смену на онлайн/не онлайн.
   *
   * @param online - онлайн или не онлайн?
   */
  void setNewState(boolean online);
}
