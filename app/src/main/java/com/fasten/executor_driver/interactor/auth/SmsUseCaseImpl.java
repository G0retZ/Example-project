package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import javax.inject.Inject;

public class SmsUseCaseImpl implements SmsUseCase {

  @NonNull
  private final SmsGateway gateway;
  @NonNull
  private final Validator<String> phoneNumberValidator;
  @Nullable
  private String phoneNumber;

  @Inject
  public SmsUseCaseImpl(@NonNull SmsGateway gateway,
      @NonNull DataSharer<String> phoneNumberSharer,
      @NonNull Validator<String> phoneNumberValidator) {
    this.gateway = gateway;
    this.phoneNumberValidator = phoneNumberValidator;
    phoneNumberSharer.get().subscribe(phoneNumber -> this.phoneNumber = phoneNumber);
  }

  @NonNull
  @Override
  public Completable sendMeCode() {
    return Completable.create(e -> {
      phoneNumberValidator.validate(phoneNumber);
      gateway.sendMeCode(phoneNumber == null ? "" : phoneNumber)
          .subscribe(e::onComplete, e::onError);
    });
  }
}
