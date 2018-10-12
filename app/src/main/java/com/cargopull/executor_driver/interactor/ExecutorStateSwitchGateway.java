package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorState;
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
