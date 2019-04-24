package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;

/**
 * Юзкейс обновления данных из другого юзкейса.
 */
public interface DataUpdateUseCase<D> {

  /**
   * Обновляет текущие данные.
   */
  void updateWith(@NonNull D data);
}
