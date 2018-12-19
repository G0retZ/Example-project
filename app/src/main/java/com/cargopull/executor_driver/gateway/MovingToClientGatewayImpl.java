package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.interactor.MovingToClientGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import javax.inject.Inject;

public class MovingToClientGatewayImpl implements MovingToClientGateway {

  @NonNull
  private final ApiService apiService;

  @Inject
  public MovingToClientGatewayImpl(@NonNull ApiService apiService) {
    this.apiService = apiService;
  }

  @NonNull
  @Override
  public Completable reportArrival() {
    return apiService.changeOrderStatus(
        Collections.singletonMap("status", "DRIVER_ARRIVED")
    ).subscribeOn(Schedulers.io());
  }
}
