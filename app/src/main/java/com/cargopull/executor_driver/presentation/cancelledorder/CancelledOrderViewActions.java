package com.cargopull.executor_driver.presentation.cancelledorder;

import androidx.annotation.NonNull;

/**
 * Действия для смены состояния вида окна отмененого предзаказа.
 */
public interface CancelledOrderViewActions {

  /**
   * Показать сообщение об отмененном предзаказе.
   *
   * @param message текст сообщения
   */
  void showCancelledOrderMessage(@NonNull String message);
}
