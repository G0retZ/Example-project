package com.cargopull.executor_driver.presentation.onlineswitch;

/**
 * Действия для смены состояния вида переключателя "онлайн".
 */
public interface OnlineSwitchViewActions {

  /**
   * Включить/выключить переключатель онлайна.
   *
   * @param check - включить или выключить?
   */
  void checkSwitch(boolean check);

  /**
   * Показать индикатор процесса.
   *
   * @param show - показать или нет?
   */
  void showSwitchPending(boolean show);
}
