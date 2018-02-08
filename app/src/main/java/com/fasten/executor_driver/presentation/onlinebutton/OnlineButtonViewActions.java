package com.fasten.executor_driver.presentation.onlinebutton;

import android.support.annotation.Nullable;

/**
 * Действия для смены состояния вида кнопки выхода на линию.
 */
public interface OnlineButtonViewActions {

  /**
   * Сделать кнопку "отзывчивой". "Отзывчивая" кнопка обрабатывает нажатия, "Неотзывчатая" - нет.
   * При этом анимация нажатия должна присутствовать в любом случае.
   *
   * @param responsive - "отзывчивость"
   */
  void setOnlineButtonResponsive(boolean responsive);

  /**
   * Показать ошибку.
   *
   * @param error - ошибка.
   */
  void showGoOnlineError(@Nullable Throwable error);
}
