package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Completable;

/**
 * Юзкейс ввода пароля.
 */
public interface PasswordUseCase {

  /**
   * Проверяет формат пароля, и запрашивает авторизацию.
   *
   * @param password {@link String} пароль для входа
   * @param afterValidation {@link Completable} продолжать ли после успешной валидации?
   * @return {@link Completable} результат проверки или входа
   */
  @NonNull
  Completable authorize(@Nullable String password, @NonNull Completable afterValidation);
}
