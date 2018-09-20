package com.cargopull.executor_driver.presentation.upcomingpreordermessage;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна сообщения о предстоящем предзаказе.
 */
public interface UpcomingPreOrderMessageViewActions {

  /**
   * Показать сообщение о предстоящем предзаказе.
   *
   * @param message текст сообщения
   */
  void showUpcomingPreOrderMessage(@NonNull String message);
}
