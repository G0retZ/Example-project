package com.cargopull.executor_driver.presentation.code;

import androidx.annotation.DrawableRes;

/**
 * Действия для смены состояния вида окна ввода кода.
 */
public interface CodeViewActions {

  /**
   * Сделать поле ввода редактируемым.
   *
   * @param enable - редактируемое или нет?
   */
  void enableInputField(boolean enable);

  /**
   * Задать картинку подчеркивания.
   *
   * @param resId реусрс картинки подчеркивания
   */
  void setUnderlineImage(@DrawableRes int resId);

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showCodeCheckPending(boolean pending);

  /**
   * Показать ошибку проверки кода.
   *
   * @param show - показать или нет?
   */
  void showCodeCheckError(boolean show);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showCodeCheckNetworkErrorMessage(boolean show);
}
