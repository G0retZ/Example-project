package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.interactor.map.HeatMapGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class HeatMapGatewayImpl implements HeatMapGateway {

  @NonNull
  private final ApiService api;

  @Inject
  public HeatMapGatewayImpl(@NonNull ApiService api) {
    this.api = api;
  }

  @NonNull
  @Override
  public Single<String> getHeatMap() {
    return api.getHeatMap()
        .subscribeOn(Schedulers.io());
  }
}
