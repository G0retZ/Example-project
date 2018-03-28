package com.fasten.executor_driver.interactor;

import io.reactivex.Completable;

/**
 * Гейтвей вебсокета.
 */
public interface SocketGateway {

  /**
   * Открывает сокет.
   */
  Completable openSocket();

  /**
   * Закрывает сокет.
   */
  void closeSocket();
}
