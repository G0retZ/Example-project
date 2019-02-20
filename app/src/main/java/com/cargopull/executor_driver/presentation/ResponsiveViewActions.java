package com.cargopull.executor_driver.presentation;

import androidx.annotation.IdRes;

/**
 * Действия для смены состояния вида окна с интерактивными элементами.
 */
public interface ResponsiveViewActions {

  /**
   * Задать активность элемента вида.
   *
   * @param id - ИД вида элемента
   * @param enable - активировть или нет?
   */
  void setEnabled(@IdRes int id, boolean enable);
}
