package com.fasten.executor_driver.presentation.movingtoclient;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface MovingToClientViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showMovingToClientPending(boolean pending);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showNetworkErrorMessage(boolean show);
}
