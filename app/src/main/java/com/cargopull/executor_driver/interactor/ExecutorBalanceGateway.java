package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import io.reactivex.Flowable;

/**
 * Гейтвей баланса исполнителя.
 */
public interface ExecutorBalanceGateway {

  /**
   * Получать баланс исполнителя.
   *
   * @param channelId - ID "канала", откуда брать баланс.
   * @return {@link Flowable<ExecutorBalanceGateway>} результат запроса.
   */
  @NonNull
  Flowable<ExecutorBalance> loadExecutorBalance(@NonNull String channelId);
}
