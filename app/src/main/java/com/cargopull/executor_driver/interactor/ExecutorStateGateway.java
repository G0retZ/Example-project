package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorState;
import io.reactivex.Flowable;

/**
 * Гейтвей получения статуса исполнителя.
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
