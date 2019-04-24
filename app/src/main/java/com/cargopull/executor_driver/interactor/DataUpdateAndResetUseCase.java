package com.cargopull.executor_driver.interactor;

/**
 * Юзкейс обновления и сброса данных из другого юзкейса.
 */
public interface DataUpdateAndResetUseCase<D> extends DataUpdateUseCase<D> {

  /**
   * Сбрасывает текущие данные.
   */
  void reset();
}
