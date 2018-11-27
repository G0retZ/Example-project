package com.cargopull.executor_driver.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * Действия для смены состояния вида окна с элементами.
 */
public interface DialogViewActions {

  /**
   * Спрятать диалог.
   */
  void dismissDialog();

  /**
   * Показать диалог.
   *
   * @param stringId - ИД текстового ресурса для сообщения
   * @param okAction - действие при нажатии кнопки "ОК" на диалоге
   */
  void showPersistentDialog(@StringRes int stringId, @Nullable Runnable okAction);

  /**
   * Показать диалог.
   *
   * @param message - текст для сообщения
   * @param okAction - действие при нажатии кнопки "ОК" на диалоге
   */
  void showPersistentDialog(@NonNull String message, @Nullable Runnable okAction);
}
