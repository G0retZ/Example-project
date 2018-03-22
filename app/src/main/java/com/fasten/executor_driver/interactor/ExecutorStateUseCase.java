package com.fasten.executor_driver.interactor;

import io.reactivex.Completable;

/**
 * Юзкейс запроса статуса исполнителя.
 */
public interface ExecutorStateUseCase {

  /**
   * Загружает статус пользователя.
   */
  Completable loadStatus();
}
