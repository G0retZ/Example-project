package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.entity.LoginData;

import io.reactivex.Completable;

/**
 * Гейтвей входа
 */
public interface PasswordGateway {

  /**
   * Запрашивает авторизацию в общей системе.
   *
   * @param loginData {@link LoginData} данные для входа.
   * @return {@link Completable} результат входа.
   */
  @NonNull
  Completable authorize(@NonNull LoginData loginData);
}
