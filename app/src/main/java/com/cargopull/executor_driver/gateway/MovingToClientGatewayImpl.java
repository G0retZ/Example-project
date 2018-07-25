package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.interactor.MovingToClientGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class MovingToClientGatewayImpl implements MovingToClientGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public MovingToClientGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable reportArrival() {
    return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"DRIVER_ARRIVED\"")
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
