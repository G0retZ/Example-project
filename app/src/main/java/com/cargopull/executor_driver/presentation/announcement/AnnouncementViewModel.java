package com.cargopull.executor_driver.presentation.announcement;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel объявлений от FCM.
 */
public interface AnnouncementViewModel extends ViewModel<AnnouncementStateViewActions> {

  /**
   * Сообщает о том что объявление прочитано.
   */
  void announcementConsumed();

  /**
   * Передает новое объявление для отображения.
   */
  void postMessage(@NonNull String message);
}
