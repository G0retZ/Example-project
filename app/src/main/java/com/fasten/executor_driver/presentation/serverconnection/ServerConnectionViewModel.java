package com.fasten.executor_driver.presentation.serverconnection;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel подключения к серверу.
 */
interface ServerConnectionViewModel extends ViewModel<ServerConnectionViewActions> {

  /**
   * Запрашивает подключение к серверу.
   */
  void connectServer();
}
