package com.fasten.executor_driver.presentation.serverconnection;

/**
 * Действия для смены состояния вида окна подключения к серверу.
 */
interface ServerConnectionViewActions {

  /**
   * Показать текущее состояние подключения.
   *
   * @param connected - есть ли подключение к серверу?
   */
  void showConnectionReady(boolean connected);
}
