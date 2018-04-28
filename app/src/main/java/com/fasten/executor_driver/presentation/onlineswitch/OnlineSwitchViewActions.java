package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

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

  /**
   * Показать ошибку.
   *
   * @param messageId - ИД текста сообщения об ошибке. -1 = не показывать ошибку.
   * @param retrySocket - можно ли решить передергиванием сокета?
   */
  void showError(@Nullable @StringRes Integer messageId, boolean retrySocket);
}
