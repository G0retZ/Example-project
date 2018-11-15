package com.cargopull.executor_driver.presentation;

import androidx.annotation.NonNull;

/**
 * Действия для смены состояния вида окна с блокирующими элементами.
 */
public interface BlockingViewActions {

  /**
   * Показать блокирующий индикатор процесса.
   *
   * @param blockerId - Уникальный ИД блокирующего.
   */
  void blockWithPending(@NonNull String blockerId);

  /**
   * Скрыть блокирующий индикатор процесса.
   *
   * @param blockerId - Уникальный ИД блокирующего.
   */
  void unblockWithPending(@NonNull String blockerId);
}
