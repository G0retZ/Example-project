package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.StringRes;

/**
 * Действия для смены состояния вида переключателя "онлайн".
 */
interface OnlineSwitchViewActions {

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

  /**
   * Показать ошибку.
   *
   * @param messageId - ИД текста сообщения об ошибке. -1 = не показывать ошибку.
   */
  void showError(@StringRes int messageId);
}
