package com.cargopull.executor_driver.interactor.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
