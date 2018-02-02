package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.interactor.auth.LoginGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class LoginGatewayImpl implements LoginGateway {

  @NonNull
  private final ApiService api;

  @Inject
  public LoginGatewayImpl(@NonNull ApiService api) {
    this.api = api;
  }

  @NonNull
  @Override
  public Completable checkLogin(@NonNull String login) {
    return api.checkLogin(login)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
