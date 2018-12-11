package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.interactor.WaitingForClientGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class WaitingForClientGatewayImpl implements WaitingForClientGateway {

  @NonNull
  private final ApiService apiService;

  @Inject
  public WaitingForClientGatewayImpl(@NonNull ApiService apiService) {
    this.apiService = apiService;
  }

  @NonNull
  @Override
  public Completable startTheOrder() {
    return apiService.startOrder().subscribeOn(Schedulers.io());
  }
}
