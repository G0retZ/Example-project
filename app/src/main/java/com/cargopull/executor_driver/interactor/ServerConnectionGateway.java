package com.cargopull.executor_driver.interactor;

import io.reactivex.Flowable;

/**
 * Гейтвей вебсокета.
 */
public interface ServerConnectionGateway {

  /**
   * Открывает сокет. Первое значение говорит об успешном соединении. Чтобы закрыть сеодинение,
   * нужно отписаться.
   */
  Flowable<Boolean> getSocketState();
}
