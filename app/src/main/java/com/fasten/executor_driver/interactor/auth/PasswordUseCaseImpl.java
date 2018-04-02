package com.fasten.executor_driver.interactor.auth;

import android.annotation.SuppressLint;
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
  private LoginData loginData = new LoginData("", "");

  @Inject
  public PasswordUseCaseImpl(@NonNull PasswordGateway gateway,
      @NonNull DataReceiver<String> loginReceiver,
      @NonNull Validator<String> passwordValidator) {
    this.gateway = gateway;
    this.passwordValidator = passwordValidator;
    loadLogin(loginReceiver);
  }

  @SuppressLint("CheckResult")
  private void loadLogin(@NonNull DataReceiver<String> loginReceiver) {
    loginReceiver.get()
        .doAfterTerminate(() -> loadLogin(loginReceiver))
        .subscribe(login -> loginData = loginData.setLogin(login),
            throwable -> {
            });
  }

  @NonNull
  @Override
  public Completable authorize(@Nullable String password, @NonNull Completable afterValidation) {
    return Completable.create(e -> {
      passwordValidator.validate(password);
      loginData = loginData.setPassword(password == null ? "" : password);
      afterValidation.subscribe(
          () -> gateway.authorize(loginData).subscribe(e::onComplete, e::onError),
          throwable -> e.onComplete()
      );
    });
  }
}
