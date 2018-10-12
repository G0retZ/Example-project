package com.cargopull.executor_driver.interactor.auth;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.LoginData;
import io.reactivex.Completable;

/**
 * Гейтвей входа.
 */
public interface PasswordGateway {

  /**
   * Запрашивает авторизацию в общей системе.
   *
   * @param loginData {@link LoginData} данные для входа
   * @return {@link Completable} результат входа
   */
  @NonNull
  Completable authorize(@NonNull LoginData loginData);
}
