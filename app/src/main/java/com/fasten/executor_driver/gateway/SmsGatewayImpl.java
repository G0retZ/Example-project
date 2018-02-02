package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.interactor.auth.SmsGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class SmsGatewayImpl implements SmsGateway {

  @NonNull
  private final ApiService api;

  @Inject
  public SmsGatewayImpl(@NonNull ApiService api) {
    this.api = api;
  }

  @NonNull
  @Override
  public Completable sendMeCode(@NonNull String phoneNumber) {
    return api.sendMeCode(phoneNumber)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
