package com.fasten.executor_driver.presentation.smsbutton;

import android.support.annotation.Nullable;

/**
 * Действия для смены состояния вида кнопки отправки СМС с таймаутом.
 */
public interface SmsButtonViewActions {

  /**
   * Задать оставшееся время до активации.
   *
   * @param secondsLeft {@link Integer} сколько секунд осталось. Для выключения счетчика передай
   * null.
   */
  void showSmsButtonTimer(@Nullable Long secondsLeft);

  /**
   * Сделать кнопку "отзывчивой". "Отзывчивая" кнопка обрабатывает нажатия, "Неотзывчатая" - нет.
   * При этом анимация нажатия должна присутствовать в любом случае.
   *
   * @param responsive - "отзывчивость"
   */
  void setSmsButtonResponsive(boolean responsive);

  /**
   * Показать ошибку.
   *
   * @param error - ошибка.
   */
  void showSmsSendError(@Nullable Throwable error);

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showSmsSendPending(boolean pending);
}
