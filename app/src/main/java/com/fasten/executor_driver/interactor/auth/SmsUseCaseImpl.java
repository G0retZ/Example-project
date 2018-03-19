package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.interactor.DataReceiver;
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
      @NonNull DataReceiver<String> phoneNumberReceiver,
      @NonNull Validator<String> phoneNumberValidator) {
    this.gateway = gateway;
    this.phoneNumberValidator = phoneNumberValidator;
    loadPhoneNumber(phoneNumberReceiver);
  }

  private void loadPhoneNumber(@NonNull DataReceiver<String> phoneNumberReceiver) {
    phoneNumberReceiver.get()
        .doAfterTerminate(() -> loadPhoneNumber(phoneNumberReceiver))
        .subscribe(phoneNumber -> this.phoneNumber = phoneNumber,
            throwable -> {
            }
        );
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
