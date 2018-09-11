package com.cargopull.executor_driver.presentation.upcomingpreorder;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна сообщения о предстоящем предзаказе.
 */
public interface UpcomingPreOrderViewActions {

  /**
   * Показать сообщение о предстоящем предзаказе.
   *
   * @param message текст сообщения
   */
  void showUpcomingPreOrderMessage(@NonNull String message);
}
