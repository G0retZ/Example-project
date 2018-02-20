package com.fasten.executor_driver.presentation.onlinebutton;

import android.support.annotation.Nullable;

/**
 * Действия для смены состояния вида кнопки выхода на линию.
 */
public interface OnlineButtonViewActions {

  /**
   * Сделать кнопку "Выйти на линию" нажимаемой
   *
   * @param enable - нажимаема или нет?
   */
  void enableGoOnlineButton(boolean enable);

  /**
   * Показать ошибку.
   *
   * @param error - ошибка.
   */
  void showGoOnlineError(@Nullable Throwable error);
}
