package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Completable;

/**
 * Юзкейс проверки имени для входа
 */
public interface LoginUseCase {

  /**
   * Проверяет формат логина.
   *
   * @param login {@link String} логин
   * @return {@link Completable} результат проверки
   */
  @NonNull
  Completable validateLogin(@Nullable String login);
}
