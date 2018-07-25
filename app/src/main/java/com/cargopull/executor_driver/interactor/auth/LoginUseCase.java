package com.cargopull.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Completable;

/**
 * Юзкейс проверки имени для входа.
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

  /**
   * Запоминает последний валидированный пароль или null (т.е. забывает), для его восстановления
   * после поворота или реинкарнации.
   *
   * @return {@link Completable} результат запоминания
   */
  Completable rememberLogin();
}
