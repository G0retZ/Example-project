package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.interactor.OrderRouteGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderRouteGatewayImpl implements OrderRouteGateway {

  @NonNull
  private final ApiService apiService;

  @Inject
  public OrderRouteGatewayImpl(@NonNull ApiService apiService) {
    this.apiService = apiService;
  }

  @NonNull
  @Override
  public Completable closeRoutePoint(@NonNull RoutePoint routePoint) {
    return apiService.completeRoutePoint(routePoint.getId()).subscribeOn(Schedulers.io());
  }

  @NonNull
  @Override
  public Completable completeTheOrder() {
    return apiService.completeOrder().subscribeOn(Schedulers.io());
  }

  @NonNull
  @Override
  public Completable nextRoutePoint(@NonNull RoutePoint routePoint) {
    return apiService.makeRoutePointNext(routePoint.getId()).subscribeOn(Schedulers.io());
  }
}
