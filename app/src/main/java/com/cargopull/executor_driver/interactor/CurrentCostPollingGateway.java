package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Гейтвей поллинга текущей цены заказа.
 */
public interface CurrentCostPollingGateway {

  /**
   * запрашивает начало поллинга.
   *
   * @return {@link Completable} результат - окончание либо ошибка.
   */
  @NonNull
  Completable startPolling();
}
