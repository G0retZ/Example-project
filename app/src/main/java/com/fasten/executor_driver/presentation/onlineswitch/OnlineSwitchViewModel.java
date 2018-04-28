package com.fasten.executor_driver.presentation.onlineswitch;

import com.fasten.executor_driver.presentation.ViewModel;

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

  /**
   * Сигнализирует о "потреблении" ошибки.
   */
  void consumeServerError();

  /**
   * Запрашивает обновление статусов с сервера.
   */
  void refreshStates();
}
