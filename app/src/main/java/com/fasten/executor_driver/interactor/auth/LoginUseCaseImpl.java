package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.entity.Validator;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;

public class LoginUseCaseImpl implements LoginUseCase {

  @NonNull
  private final LoginGateway gateway;
  @NonNull
  private final Validator<String> loginValidator;

  @Inject
  LoginUseCaseImpl(@NonNull LoginGateway gateway,
      @Named("loginValidator") @NonNull Validator<String> loginValidator) {
    this.gateway = gateway;
    this.loginValidator = loginValidator;
  }

  @NonNull
  @Override
  public Completable validateLogin(@Nullable String login) {
    return Completable.create(e -> {
      if (loginValidator.validate(login)) {
        e.onComplete();
      } else {
        e.onError(new ValidationException());
      }
    });
  }

  @NonNull
  @Override
  public Completable checkLogin(@Nullable String login) {
    if (login == null) {
      return Completable.error(new ValidationException());
    }
    return gateway.checkLogin(login);
  }
}
