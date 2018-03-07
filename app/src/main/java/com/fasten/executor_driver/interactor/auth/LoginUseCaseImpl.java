package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import javax.inject.Inject;

public class LoginUseCaseImpl implements LoginUseCase {

  @NonNull
  private final Validator<String> loginValidator;
  @NonNull
  private final DataSharer<String> loginSharer;
  @Nullable
  private String lastLogin;

  @Inject
  public LoginUseCaseImpl(
      @NonNull DataSharer<String> loginSharer,
      @NonNull Validator<String> loginValidator) {
    this.loginValidator = loginValidator;
    this.loginSharer = loginSharer;
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
      loginSharer.share(lastLogin);
      e.onComplete();
    });
  }
}
