package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import io.reactivex.Flowable;

/**
 * Гейтвей получения таймеров поллинга из заказа.
 */
public interface CurrentCostPollingTimersGateway {

  /**
   * Ожидает таймеры поллинга в заказе для исполнителя у сокета.
   *
   * @return {@link Flowable<Pair>} таймеры поллинг в заказе для исполнителя. Первая цифра - задержа перед началом поллинга, вторая - интервал поллинга.
   */
  @NonNull
  Flowable<Pair<Long, Long>> getPollingTimers();
}
