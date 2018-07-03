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
   * Задействовать кнопку звонка клиенту.
   *
   * @param enable - задействовать или нет?
   */
  void enableMovingToClientCallButton(boolean enable);
}
