package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс потери авторизации. Слушает события из гейтвея, и если была потерена авторизация, то:
 * 1. Сообщает об этом своим подписчикам.
 * 2. Переключает состояние исполнителя.
 */
public interface UnAuthUseCase {

  /**
   * Ожидает события следующей потери авторизации от гейтвея.
   *
   * @return {@link Completable} событие потери авторизации.
   */
  @NonNull
  Completable getUnauthorized();
}
