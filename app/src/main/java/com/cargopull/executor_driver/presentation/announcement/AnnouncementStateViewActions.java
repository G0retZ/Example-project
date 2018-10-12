package com.cargopull.executor_driver.presentation.announcement;

import androidx.annotation.NonNull;

/**
 * Действия для смены состояния вида объявлений.
 */
public interface AnnouncementStateViewActions {

  /**
   * Показать объявление.
   *
   * @param message текст сообщения
   */
  void showAnnouncementMessage(@NonNull String message);
}
