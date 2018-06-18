package com.fasten.executor_driver.interactor;

import io.reactivex.Flowable;

/**
 * Гейтвей вебсокета.
 */
public interface SocketGateway {

  /**
   * Открывает сокет. Первое значение говорит об успешном соединении.
   * Чтобы закрыть сеодинение, нужно отписаться.
   */
  Flowable<Boolean> openSocket();
}
