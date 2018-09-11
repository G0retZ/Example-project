package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import io.reactivex.Flowable;

/**
 * Юзкейс баланса исполнителя.
 */
public interface ExecutorBalanceUseCase {

  /**
   * Запрашивает баланс исполнителя, выдает последний закешированный результат, если не сброшен.
   *
   * @return {@link Flowable<CancelOrderReason>} результат запроса.
   */
  @NonNull
  Flowable<ExecutorBalance> getExecutorBalance();
}
