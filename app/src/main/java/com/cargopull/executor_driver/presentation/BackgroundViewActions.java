package com.cargopull.executor_driver.presentation;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

/**
 * Действия для смены состояния вида окна с элементами с бэкграундом.
 */
public interface BackgroundViewActions {

  /**
   * Показать картинку из ресурса в бэкграунде элемента.
   *
   * @param id - ИД вида элемента
   * @param drawableId - ИД ресурса картинки
   */
  void setBackground(@IdRes int id, @DrawableRes int drawableId);
}
