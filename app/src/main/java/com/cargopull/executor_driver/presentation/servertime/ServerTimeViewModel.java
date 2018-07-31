package com.cargopull.executor_driver.presentation.servertime;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel получения текущего времени сервера.
 */
public interface ServerTimeViewModel extends ViewModel<Runnable> {

  /**
   * Запрашивает подписку на получение текущего времени сервера.
   */
  void initializeServerTime();
}
