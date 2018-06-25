package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Гейтвей поллинга текущей цены заказа.
 */
public interface CurrentCostPollingGateway {

  /**
   * Передает сообщение поллинга.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable poll();
}
