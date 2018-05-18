package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.interactor.WaitingForClientGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class WaitingForClientGatewayImpl implements WaitingForClientGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public WaitingForClientGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable callToClient() {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"CALL_TO_CLIENT\"")
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }

  @NonNull
  @Override
  public Completable startTheOrder() {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"START_ORDER\"")
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }
}
