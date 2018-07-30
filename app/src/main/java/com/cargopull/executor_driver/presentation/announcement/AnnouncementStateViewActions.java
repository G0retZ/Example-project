package com.cargopull.executor_driver.presentation.announcement;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида объявлений.
 */
public interface AnnouncementStateViewActions {

  /**
   * Показать объявление.
   *
   * @param message текст сообщения
   */
  void showMessage(@NonNull String message);
}
