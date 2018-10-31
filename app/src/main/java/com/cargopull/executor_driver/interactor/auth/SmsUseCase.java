package com.cargopull.executor_driver.interactor.auth;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс запроса звонка с кодом.
 */
public interface SmsUseCase {

  /**
   * Валидирует номер телефона, и запрашивает на него СМС с кодом.
   *
   * @return {@link Completable} результат валидации или запроса
   */
  @NonNull
  Completable sendMeCode();
}
