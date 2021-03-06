package com.cargopull.executor_driver.interactor.auth;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Validator;
import com.cargopull.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class SmsUseCaseImpl implements SmsUseCase {

  @NonNull
  private final SmsGateway gateway;
  @NonNull
  private final Validator<String> phoneNumberValidator;
  @NonNull
  private final DataReceiver<String> phoneNumberReceiver;

  @Inject
  public SmsUseCaseImpl(@NonNull SmsGateway gateway,
      @NonNull DataReceiver<String> phoneNumberReceiver,
      @NonNull Validator<String> phoneNumberValidator) {
    this.gateway = gateway;
    this.phoneNumberValidator = phoneNumberValidator;
    this.phoneNumberReceiver = phoneNumberReceiver;
  }

  @NonNull
  @Override
  public Completable sendMeCode() {
    return phoneNumberReceiver.get()
        .firstOrError()
        .map(phoneNumber -> {
          phoneNumberValidator.validate(phoneNumber);
          return phoneNumber;
        }).flatMapCompletable(phoneNumber ->
            gateway.sendMeCode(phoneNumber == null ? "" : phoneNumber)
                .observeOn(Schedulers.single())
        );
  }
}
