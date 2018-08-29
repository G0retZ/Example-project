package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.interactor.OrderRouteGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class OrderRouteGatewayImpl implements OrderRouteGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public OrderRouteGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable closeRoutePoint(@NonNull RoutePoint routePoint) {
    return stompClient.send(
        BuildConfig.ROUTE_DESTINATION,
        "{\"complete\":\"" + routePoint.getId() + "\"}"
    )
        .subscribeOn(Schedulers.io());
  }

  @NonNull
  @Override
  public Completable completeTheOrder() {
    return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"COMPLETE_ORDER\"")
        .subscribeOn(Schedulers.io());
  }

  @NonNull
  @Override
  public Completable nextRoutePoint(@NonNull RoutePoint routePoint) {
    return stompClient.send(
        BuildConfig.ROUTE_DESTINATION,
        "{\"next\":\"" + routePoint.getId() + "\"}"
    )
        .subscribeOn(Schedulers.io());
  }
}
