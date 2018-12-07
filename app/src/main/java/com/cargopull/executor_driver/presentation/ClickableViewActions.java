package com.cargopull.executor_driver.presentation;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

/**
 * Действия для смены состояния вида окна с элементами.
 */
public interface ClickableViewActions {

  /**
   * Показать диалог.
   *
   * @param id - ИД вида элемента
   * @param okAction - действие при нажатии на кнопку
   */
  void setClickAction(@IdRes int id, @Nullable Runnable okAction);
}
