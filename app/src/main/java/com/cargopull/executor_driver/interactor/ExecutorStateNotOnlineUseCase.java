package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс смены статуса исполнителя на "смена закрыта".
 */
public interface ExecutorStateNotOnlineUseCase {

  /**
   * Запрашивает смену статуса исполнителя на "смена открыта".
   * Возващает специальную ошибку, если запрошена смена из нелегального состояния.
   *
   * @return {@link Completable} результат запроса.
   */
  @NonNull
  Completable setExecutorNotOnline();
}
