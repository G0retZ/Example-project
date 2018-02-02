package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import javax.inject.Inject;
import javax.inject.Named;

public class SmsUseCaseImpl implements SmsUseCase {

  @NonNull
  private final SmsGateway gateway;
  @NonNull
  private final Validator<String> phoneNumberValidator;
  @Nullable
  private String phoneNumber;

  @Inject
  SmsUseCaseImpl(@NonNull SmsGateway gateway,
      @Named("loginSharer") @NonNull DataSharer<String> phoneNumberSharer,
      @Named("phoneNumberValidator") @NonNull Validator<String> phoneNumberValidator) {
    this.gateway = gateway;
    this.phoneNumberValidator = phoneNumberValidator;
    phoneNumberSharer.get().subscribe(phoneNumber -> this.phoneNumber = phoneNumber);
  }

  @NonNull
  @Override
  public Completable sendMeCode() {
    return Completable.create(e -> {
      if (phoneNumber != null && phoneNumberValidator.validate(phoneNumber)) {
        gateway.sendMeCode(phoneNumber).subscribe(e::onComplete, e::onError);
      } else {
        e.onError(new ValidationException());
      }
    });
  }
}
