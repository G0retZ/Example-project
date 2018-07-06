package com.fasten.executor_driver.presentation.clientorderconfirmationtime;

import android.support.annotation.StringRes;

/**
 * Действия для смены состояния вида текущего времени ожидания подтверждения клиента.
 */
public interface ClientOrderConfirmationTimeViewActions {

  /**
   * Задать текущее значение таймера.
   *
   * @param currentMillis - текущее значение таймера в миллисекундах.
   */
  void setWaitingForClientTime(long currentMillis);

  /**
   * Задать ИД текста сообщения.
   *
   * @param stringId - ИД текста сообщения.
   */
  void setWaitingForClientTimeText(@StringRes int stringId);

  /**
   * Показать таймер обратного отсчета.
   *
   * @param show - показать или нет?
   */
  void showWaitingForClientTimer(boolean show);
}
