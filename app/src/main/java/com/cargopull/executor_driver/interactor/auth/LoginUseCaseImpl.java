package com.cargopull.executor_driver.interactor.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Validator;
import io.reactivex.Completable;
import io.reactivex.Observer;
import javax.inject.Inject;

public class LoginUseCaseImpl implements LoginUseCase {

  @NonNull
  private final Validator<String> loginValidator;
  @NonNull
  private final Observer<String> loginObserver;
  @Nullable
  private String lastLogin;

  @Inject
  public LoginUseCaseImpl(
      @NonNull Observer<String> loginObserver,
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
        loginObserver.onComplete();
      } else {
        loginObserver.onNext(lastLogin);
      }
      e.onComplete();
    });
  }
}
