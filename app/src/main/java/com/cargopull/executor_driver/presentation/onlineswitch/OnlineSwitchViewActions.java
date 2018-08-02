package com.cargopull.executor_driver.presentation.onlineswitch;

/**
 * Действия для смены состояния вида переключателя "онлайн".
 */
public interface OnlineSwitchViewActions {

  /**
   * Показать текст "Перерыв".
   *
   * @param show - показать или нет?
   */
  void showBreakText(boolean show);

  /**
   * Показать кнопку "Перерыв".
   *
   * @param show - показать или нет?
   */
  void showTakeBreakButton(boolean show);

  /**
   * Показать кнопку "Возобновить работу".
   *
   * @param show - показать или нет?
   */
  void showResumeWorkButton(boolean show);

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showSwitchPending(boolean pending);
}
