package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import javax.inject.Inject;
import javax.inject.Named;

public class PasswordUseCaseImpl implements PasswordUseCase {

  @NonNull
  private final PasswordGateway gateway;
  @NonNull
  private final Validator<String> passwordValidator;
  @NonNull
  private LoginData loginData = new LoginData("", "");

  @Inject
  PasswordUseCaseImpl(@NonNull PasswordGateway gateway,
      @Named("loginSharer") @NonNull DataSharer<String> loginSharer,
      @Named("passwordValidator") @NonNull Validator<String> passwordValidator) {
    this.gateway = gateway;
    this.passwordValidator = passwordValidator;
    loginSharer.get().subscribe(login -> loginData = loginData.setLogin(login));
  }

  @NonNull
  @Override
  public Completable authorize(@Nullable String password, @NonNull Completable afterValidation) {
    return Completable.create(e -> {
      if (password != null && passwordValidator.validate(password)) {
        loginData = loginData.setPassword(password);
        afterValidation.subscribe(
            () -> gateway.authorize(loginData).subscribe(e::onComplete, e::onError),
            throwable -> e.onComplete()
        );
      } else {
        e.onError(new ValidationException());
      }
    });
  }
}
