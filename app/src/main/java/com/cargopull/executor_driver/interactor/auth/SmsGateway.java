package com.cargopull.executor_driver.interactor.auth;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Гейтвей запроса звонка с кодом.
 */
public interface SmsGateway {

  /**
   * Запрашивает у системы СМС с кодом на номер телефона.
   *
   * @param phoneNumber {@link String} номер телефона
   * @return {@link Completable} результат запроса
   */
  @NonNull
  Completable sendMeCode(@NonNull String phoneNumber);
}
