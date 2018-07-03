package com.fasten.executor_driver.presentation.codeheader;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Действия для смены состояния вида описания ввода кода.
 */
public interface CodeHeaderViewActions {

  /**
   * Задать объясняющий заголовок.
   *
   * @param textId - ИД ресурса текста
   * @param phoneNumber {@link String} номер телефона в отформатированном виде.
   */
  void setDescriptiveHeaderText(@StringRes int textId, @NonNull String phoneNumber);
}
