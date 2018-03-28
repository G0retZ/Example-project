package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Flowable;

/**
 * Гейтвей получения статуса исполнителя.
 */
public interface ExecutorStateGateway {

  /**
   * Запрашивает статус исполнителя у АПИ.
   *
   * @param channelId - ID "канала", откуда брать статусы.
   * @return {@link Flowable<ExecutorState>} актуальный статус исполнителя.
   */
  @NonNull
  Flowable<ExecutorState> getState(@NonNull String channelId);
}
