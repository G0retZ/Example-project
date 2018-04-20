package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.LoginData;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import javax.inject.Inject;

public class PasswordUseCaseImpl implements PasswordUseCase {

  @NonNull
  private final PasswordGateway gateway;
  @NonNull
  private final Validator<String> passwordValidator;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  @Inject
  public PasswordUseCaseImpl(@NonNull PasswordGateway gateway,
      @NonNull DataReceiver<String> loginReceiver,
      @NonNull Validator<String> passwordValidator) {
    this.gateway = gateway;
    this.passwordValidator = passwordValidator;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Completable authorize(@Nullable String password, @NonNull Completable afterValidation) {
    return loginReceiver.get()
        .firstOrError()
        .map(login -> {
          passwordValidator.validate(password);
          return new LoginData(login, password == null ? "" : password);
        })
        .flatMapCompletable(loginData ->
            afterValidation.concatWith(gateway.authorize(loginData))
        );
  }
}
