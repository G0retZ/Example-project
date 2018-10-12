package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Гейтвей звонка клиенту.
 */
public interface CallToClientGateway {

  /**
   * Передает запрос звонка клиенту.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable callToClient();
}
