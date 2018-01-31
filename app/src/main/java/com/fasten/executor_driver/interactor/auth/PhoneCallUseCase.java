package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;

/**
 * Юзкейс запроса звонка с кодом
 */
public interface PhoneCallUseCase {

  /**
   * Валидирует номер телефона, и запрашивает на него звонок с кодом.
   *
   * @param phoneNumber {@link String} номер телефона.
   * @return {@link Completable} результат валидации или запроса.
   */
  @NonNull
  Completable callMe(@Nullable String phoneNumber);
}
