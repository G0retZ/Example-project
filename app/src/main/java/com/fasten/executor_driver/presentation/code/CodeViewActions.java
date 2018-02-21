package com.fasten.executor_driver.presentation.code;

import android.support.annotation.Nullable;

/**
 * Действия для смены состояния вида окна ввода кода.
 */
public interface CodeViewActions {

  /**
   * Войти в приложение (завершить авторизацию, и перейти далее).
   */
  void letIn();

  /**
   * Показать ошибку.
   *
   * @param error - ошибка
   */
  void showCodeCheckError(@Nullable Throwable error);

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showCodeCheckPending(boolean pending);
}
