package com.fasten.executor_driver.interactor;

import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Flowable;

/**
 * Юзкейс статусов исполнителя.
 */
public interface ExecutorStateUseCase {


  /**
   * Запрашивает статусы исполнителя.
   *
   * @return {@link Flowable<ExecutorState>} результат запроса.
   */
  Flowable<ExecutorState> getExecutorStates();
}
