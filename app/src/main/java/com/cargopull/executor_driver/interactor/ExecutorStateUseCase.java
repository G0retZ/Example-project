package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorState;
import io.reactivex.Flowable;

/**
 * Юзкейс статусов исполнителя.
 */
public interface ExecutorStateUseCase {

  /**
   * Запрашивает статусы исполнителя, выдает последний закешированный результат, если не сброшен.
   *
   * @return {@link Flowable<ExecutorState>} результат запроса.
   */
  @NonNull
  Flowable<ExecutorState> getExecutorStates();
}
