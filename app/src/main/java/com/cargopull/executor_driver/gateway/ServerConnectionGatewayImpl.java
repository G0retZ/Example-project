package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.stomp.StompClient;
import com.cargopull.executor_driver.interactor.ServerConnectionGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ServerConnectionGatewayImpl implements ServerConnectionGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public ServerConnectionGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @Override
  public Flowable<Boolean> getSocketState() {
    return stompClient.getConnectionState()
        .subscribeOn(Schedulers.io());
  }
}
