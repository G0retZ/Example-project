package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.outgoing.ApiLogin;
import com.cargopull.executor_driver.entity.LoginData;
import com.cargopull.executor_driver.interactor.auth.PasswordGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class PasswordGatewayImpl implements PasswordGateway {

  @NonNull
  private final ApiService api;

  @Inject
  public PasswordGatewayImpl(@NonNull ApiService api) {
    this.api = api;
  }

  @NonNull
  @Override
  public Completable authorize(@NonNull LoginData loginData) {
    return api.authorize(new ApiLogin(loginData.getLogin(), loginData.getPassword()))
        .subscribeOn(Schedulers.io());
  }
}
