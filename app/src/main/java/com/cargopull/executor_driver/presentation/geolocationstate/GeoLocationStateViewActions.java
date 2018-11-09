package com.cargopull.executor_driver.presentation.geolocationstate;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

/**
 * Действия для смены состояния вида состояния сервисов местоположения.
 */
public interface GeoLocationStateViewActions {

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
   * @param stringId - ИД текстового ресурса
   */
  void setText(@IdRes int id, @StringRes int stringId);

  /**
   * Показать картинку из ресурса в элементе.
   *
   * @param id - ИД вида элемента
   * @param drawableId - ИД ресурса картинки
   */
  void setImage(@IdRes int id, @DrawableRes int drawableId);
}
