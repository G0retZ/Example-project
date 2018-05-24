package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.interactor.OrderRouteGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.client.StompClient;

public class OrderRouteGatewayImpl implements OrderRouteGateway {

  @NonNull
  private final StompClient stompClient;

  public OrderRouteGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable checkRoutePoint(@NonNull RoutePoint routePoint, boolean check) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(
          BuildConfig.TRIP_DESTINATION,
          "{\"id\":\"" + routePoint.getId() + "\", \"checked\":\"" + check + "\"}"
      )
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }
}
