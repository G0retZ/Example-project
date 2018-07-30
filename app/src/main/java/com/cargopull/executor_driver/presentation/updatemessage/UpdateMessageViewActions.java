package com.cargopull.executor_driver.presentation.updatemessage;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна сообщений о новой версии приложения.
 */
interface UpdateMessageViewActions {

  /**
   * Показать сообщение о новой версии приложения.
   *
   * @param message текст сообщения
   */
  void showUpdateMessage(@NonNull String message);
}
