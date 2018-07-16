package com.cargopull.executor_driver.presentation.onlinebutton;

/**
 * Действия для смены состояния вида кнопки выхода на линию.
 */
public interface OnlineButtonViewActions {

  /**
   * Сделать кнопку "Выйти на линию" нажимаемой.
   *
   * @param enable - нажимаема или нет?
   */
  void enableGoOnlineButton(boolean enable);

  /**
   * Показать индикатор процесса.
   *
   * @param show - показать или нет?
   */
  void showGoOnlinePending(boolean show);
}
