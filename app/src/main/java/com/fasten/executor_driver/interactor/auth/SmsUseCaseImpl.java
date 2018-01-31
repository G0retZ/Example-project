package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.entity.Validator;

import javax.inject.Inject;

import io.reactivex.Completable;
import javax.inject.Named;

public class SmsUseCaseImpl implements SmsUseCase {

  @NonNull
  private final SmsGateway gateway;
  @NonNull
  private final Validator<String> phoneNumberValidator;

  @Inject
  SmsUseCaseImpl(@NonNull SmsGateway gateway,
      @Named("phoneNumberValidator") @NonNull Validator<String> phoneNumberValidator) {
    this.gateway = gateway;
    this.phoneNumberValidator = phoneNumberValidator;
  }

  @NonNull
  @Override
  public Completable sendMeCode(@Nullable String phoneNumber) {
    return Completable.create(e -> {
      if (phoneNumber != null && phoneNumberValidator.validate(phoneNumber)) {
        gateway.sendMeCode(phoneNumber).subscribe(e::onComplete, e::onError);
      } else {
        e.onError(new ValidationException());
      }
    });
  }
}
