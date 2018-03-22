package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Single;

/**
 * Гейтвей получения статуса пользователя.
 */
public interface ExecutorStateGateway {

  /**
   * Запрашивает статус исполнителя у АПИ.
   *
   * @return {@link Single<ExecutorState>} актуальный статус исполнителя.
   */
  @NonNull
  Single<ExecutorState> getState();
}
