package com.cargopull.executor_driver.presentation.serverconnection;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel подключения к серверу.
 */
public interface ServerConnectionViewModel extends ViewModel<ServerConnectionViewActions> {

  /**
   * Запрашивает подключение к серверу.
   */
  void connectServer();
}
