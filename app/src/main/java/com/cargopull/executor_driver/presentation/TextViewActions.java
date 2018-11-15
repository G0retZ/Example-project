package com.cargopull.executor_driver.presentation;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * Действия для смены состояния вида окна с текствыми элементами.
 */
public interface TextViewActions {

  /**
   * Задать текст в элементе.
   *
   * @param id - ИД вида элемента
   * @param text - текст для отображения
   */
  void setText(@IdRes int id, @NonNull String text);

  /**
   * Задать текст в элементе.
   *
   * @param id - ИД вида элемента
   * @param stringId - ИД текстового ресурса
   */
  void setText(@IdRes int id, @StringRes int stringId);

  /**
   * Задать форматированный текст в элементе.
   *
   * @param id - ИД вида элемента
   * @param stringId - ИД текстового ресурса
   * @param formatArgs - аргументы форматирования
   */
  void setFormattedText(@IdRes int id, @StringRes int stringId, Object... formatArgs);

  /**
   * Задать цвет текста в элементе.
   *
   * @param id - ИД вида элемента
   * @param colorId - ИД цветового ресурса
   */
  void setTextColor(@IdRes int id, @ColorRes int colorId);
}
