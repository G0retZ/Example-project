package com.cargopull.executor_driver.interactor.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.LoginData;
import com.cargopull.executor_driver.entity.Validator;
import com.cargopull.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
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
            afterValidation.observeOn(Schedulers.single())
                .concatWith(gateway.authorize(loginData).observeOn(Schedulers.single()))
        );
  }
}
