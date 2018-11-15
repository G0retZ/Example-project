package com.cargopull.executor_driver.presentation;

import androidx.annotation.IdRes;

/**
 * Действия для смены состояния вида окна с исчезающими элементами.
 */
public interface VisibilityViewActions {

  /**
   * Задать видимость элемента вида.
   *
   * @param id - ИД вида элемента
   * @param visible - показать или спрятать?
   */
  void setVisible(@IdRes int id, boolean visible);
}
