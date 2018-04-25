package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Flowable;

/**
 * Юзкейс статусов исполнителя.
 */
public interface ExecutorStateUseCase {

  /**
   * Запрашивает статусы исполнителя, выдает последний закешированный результат, если не сброшен.
   *
   * @param reset - сбросить ли кеш? Сбрасывает кеш для всех последующих запросов к юзкейсу.
   * @return {@link Flowable<ExecutorState>} результат запроса.
   */
  @NonNull
  Flowable<ExecutorState> getExecutorStates(boolean reset);
}
