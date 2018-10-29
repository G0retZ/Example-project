package com.cargopull.executor_driver.presentation;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * Действия для смены состояния вида окна с элементами.
 */
public interface ViewActions {

  /**
   * Показать блокирующий индикатор процесса.
   *
   * @param blockerId - Уникальный ИД блокирующего.
   */
  void blockWithPending(@NonNull String blockerId);

  /**
   * Скрыть блокирующий индикатор процесса.
   *
   * @param blockerId - Уникальный ИД блокирующего.
   */
  void unblockWithPending(@NonNull String blockerId);

  /**
   * Показать элемент вида.
   *
   * @param id - ИД вида элемента
   */
  void showView(@IdRes int id);

  /**
   * Спрятать элемент вида.
   *
   * @param id - ИД вида элемента
   */
  void hideView(@IdRes int id);

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

  /**
   * Показать число в элементе.
   *
   * @param id - ИД вида элемента
   * @param drawableId - ИД ресурса картинки
   */
  void setImage(@IdRes int id, @DrawableRes int drawableId);
}
