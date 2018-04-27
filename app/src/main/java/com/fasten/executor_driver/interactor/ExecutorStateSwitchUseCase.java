package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;

/**
 * Юзкейс смены статуса исполнителя.
 */
interface ExecutorStateSwitchUseCase {

  /**
   * Запрашивает смену статуса исполнителя.
   *
   * @param executorState - новый статус исполнителя.
   * @return {@link Completable} результат запроса.
   */
  @NonNull
  Completable setExecutorState(ExecutorState executorState);
}
