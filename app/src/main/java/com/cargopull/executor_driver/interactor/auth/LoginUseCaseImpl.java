package com.cargopull.executor_driver.interactor.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Validator;
import com.cargopull.executor_driver.interactor.DataUpdateAndResetUseCase;
import io.reactivex.Completable;
import javax.inject.Inject;

public class LoginUseCaseImpl implements LoginUseCase {

  @NonNull
  private final Validator<String> loginValidator;
  @NonNull
  private final DataUpdateAndResetUseCase<String> loginObserver;
  @Nullable
  private String lastLogin;

  @Inject
  public LoginUseCaseImpl(
      @NonNull DataUpdateAndResetUseCase<String> loginObserver,
      @NonNull Validator<String> loginValidator) {
    this.loginValidator = loginValidator;
    this.loginObserver = loginObserver;
  }

  @NonNull
  @Override
  public Completable validateLogin(@Nullable String login) {
    return Completable.create(e -> {
      loginValidator.validate(login);
      lastLogin = login;
      e.onComplete();
    });
  }

  @Override
  public Completable rememberLogin() {
    return Completable.create(e -> {
      if (lastLogin == null) {
        loginObserver.reset();
      } else {
        loginObserver.updateWith(lastLogin);
      }
      e.onComplete();
    });
  }
}
