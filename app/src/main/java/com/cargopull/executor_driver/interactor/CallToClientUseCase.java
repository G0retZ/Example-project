package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс звонка клиенту.
 */
public interface CallToClientUseCase {

  /**
   * Запрашивает звонок клиенту.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable callToClient();
}
