package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;

/**
 * Гейтвей передачи серверу нового статуса исполнителя.
 */
public interface ExecutorStateSwitchGateway {

  /**
   * Передает новый статус исполнителя серверу.
   *
   * @param executorState геопозиция для передачи.
   * @return {@link Completable} результат передачи.
   */
  @NonNull
  Completable sendNewExecutorState(ExecutorState executorState);
}
