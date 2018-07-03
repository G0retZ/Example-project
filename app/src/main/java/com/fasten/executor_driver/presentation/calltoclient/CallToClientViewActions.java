package com.fasten.executor_driver.presentation.calltoclient;

/**
 * Действия для смены состояния вида окна звонка клиенту.
 */
public interface CallToClientViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showCallToClientPending(boolean pending);

  /**
   * Показать статус звонка.
   *
   * @param calling - звоним или нет?
   */
  void showCallingToClient(boolean calling);
}
