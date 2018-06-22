package com.fasten.executor_driver.presentation.waitingforclient;

/**
 * Действия для смены состояния вида окна ожидания клиента.
 */
public interface WaitingForClientViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showWaitingForClientPending(boolean pending);
}
