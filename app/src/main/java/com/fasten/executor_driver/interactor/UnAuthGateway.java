package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Гейтвей потери авторизации.
 */
public interface UnAuthGateway {

  /**
   * Закладывает триггер на потерю авторизации.
   *
   * @return {@link Completable} событие потери авторизации.
   */
  @NonNull
  Completable waitForUnauthorized();
}
