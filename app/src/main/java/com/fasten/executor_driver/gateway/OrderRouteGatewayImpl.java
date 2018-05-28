package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.interactor.OrderRouteGateway;
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
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(
          BuildConfig.TRIP_DESTINATION,
          "{\"close\":\"" + routePoint.getId() + "\"}"
      )
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }

  @NonNull
  @Override
  public Completable completeTheOrder() {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"COMPLETE_ORDER\"")
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }

  @NonNull
  @Override
  public Completable nextRoutePoint(@NonNull RoutePoint routePoint) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(
          BuildConfig.TRIP_DESTINATION,
          "{\"next\":\"" + routePoint.getId() + "\"}"
      )
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }
}
