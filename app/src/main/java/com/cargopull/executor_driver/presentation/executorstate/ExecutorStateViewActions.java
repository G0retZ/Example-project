package com.cargopull.executor_driver.presentation.executorstate;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна заставки.
 */
public interface ExecutorStateViewActions {

  /**
   * Показать сопуствующее сообщение с объяснением (смены) статуса
   *
   * @param message текст сообщения
   */
  void showOnlineMessage(@NonNull String message);
}