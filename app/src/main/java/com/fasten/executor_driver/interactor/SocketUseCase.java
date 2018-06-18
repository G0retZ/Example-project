package com.fasten.executor_driver.interactor;

import io.reactivex.Flowable;

/**
 * Юзкейс вебсокета.
 */
interface SocketUseCase {

  /**
   * Соединяет с сокетом сервера. Первое значение говорит об успешном соединении.
   * Чтобы закрыть сеодинение, нужно отписаться.
   */
  Flowable<Boolean> connect();
}
