package com.cargopull.executor_driver.presentation.executorstate;

import androidx.annotation.NonNull;

/**
 * Действия для смены состояния вида окна заставки.
 */
public interface ExecutorStateViewActions {

  /**
   * Показать сопуствующее сообщение смены статуса
   *
   * @param message текст сообщения
   */
  void showExecutorStatusMessage(@NonNull String message);

  /**
   * Показать расшифровку статуса
   *
   * @param message текст сообщения
   */
  void showExecutorStatusInfo(@NonNull String message);
}
