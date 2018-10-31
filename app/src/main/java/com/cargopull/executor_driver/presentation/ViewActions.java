package com.cargopull.executor_driver.presentation;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
   * Задать видимость элемента вида.
   *
   * @param id - ИД вида элемента
   * @param visible - показать или спрятать?
   */
  void setVisible(@IdRes int id, boolean visible);

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

  /**
   * Спрятать диалог.
   */
  void dismissDialog();

  /**
   * Показать диалог.
   *
   * @param stringId - ИД текстового ресурса для сообщения
   */
  void showPersistentDialog(@StringRes int stringId, @Nullable Runnable okAction);

  /**
   * Показать диалог.
   *
   * @param message - текст для сообщения
   */
  void showPersistentDialog(@NonNull String message, @Nullable Runnable okAction);
}
