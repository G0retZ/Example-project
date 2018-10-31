package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс получения текущего времени сервера.
 */
public interface ServerTimeUseCase {

  /**
   * Запрашивает потребление текущих временных меток сервера.
   *
   * @return {@link Completable} результат запроса - ошибка или окончание данных.
   */
  @NonNull
  Completable getServerTime();
}
