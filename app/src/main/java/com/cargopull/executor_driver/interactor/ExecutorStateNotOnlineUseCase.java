package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Юзкейс смены статуса исполнителя на "смена закрыта".
 */
public interface ExecutorStateNotOnlineUseCase {

  /**
   * Запрашивает статусы исполнителя, выдает последний закешированный результат.
   *
   * @return {@link Flowable <ExecutorState>} результат запроса.
   */
  @NonNull
  Flowable<ExecutorState> getExecutorStates();

  /**
   * Запрашивает смену статуса исполнителя на "смена открыта".
   * Возващает специальную ошибку, если запрошена смена из нелегального состояния.
   *
   * @return {@link Completable} результат запроса.
   */
  @NonNull
  Completable setExecutorNotOnline();
}
