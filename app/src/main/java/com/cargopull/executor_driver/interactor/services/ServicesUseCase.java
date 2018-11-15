package com.cargopull.executor_driver.interactor.services;

import io.reactivex.Completable;

/**
 * Юзкейс назначения услуг исполнителя.
 */
public interface ServicesUseCase {

  /**
   * Назначает все доступные услуги исполнителю для выхода на линию.
   *
   * @return {@link Completable} результат назначения и выхода на линию
   */
  Completable autoAssignServices();
}
