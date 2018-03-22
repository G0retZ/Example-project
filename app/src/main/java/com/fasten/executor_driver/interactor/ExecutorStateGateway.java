package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Flowable;

/**
 * Гейтвей получения статуса пользователя.
 */
public interface ExecutorStateGateway {

  /**
   * Запрашивает статус исполнителя у АПИ.
   *
   * @return {@link Flowable<ExecutorState>} актуальный статус исполнителя.
   */
  @NonNull
  Flowable<ExecutorState> getState();
}
