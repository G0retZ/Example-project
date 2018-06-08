package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.CancelOrderReason;
import com.fasten.executor_driver.entity.ExecutorBalance;
import io.reactivex.Flowable;

/**
 * Юзкейс баланса исполнителя.
 */
public interface ExecutorBalanceUseCase {

  /**
   * Запрашивает баланс исполнителя, выдает последний закешированный результат, если не сброшен.
   *
   * @param reset - сбросить ли кеш? Сбрасывает кеш для всех последующих запросов к юзкейсу.
   * @return {@link Flowable<CancelOrderReason>} результат запроса.
   */
  @NonNull
  Flowable<ExecutorBalance> getExecutorBalance(boolean reset);
}
