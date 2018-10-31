package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Гейтвей работы с заказами при движении к клиенту.
 */
public interface WaitingForClientGateway {

  /**
   * Передает сообщение о начале исполнения заказа.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable startTheOrder();
}
