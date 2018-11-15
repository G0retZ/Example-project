package com.cargopull.executor_driver.presentation;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

/**
 * Действия для смены состояния вида окна с блокирующими элементами.
 */
public interface ImageViewActions {

  /**
   * Показать картинку из ресурса в элементе.
   *
   * @param id - ИД вида элемента
   * @param drawableId - ИД ресурса картинки
   */
  void setImage(@IdRes int id, @DrawableRes int drawableId);

  /**
   * Показать картинку из веба в элементе.
   *
   * @param id - ИД вида элемента
   * @param drawableUrl - URL картинки
   */
  void setImage(@IdRes int id, @NonNull String drawableUrl);
}
