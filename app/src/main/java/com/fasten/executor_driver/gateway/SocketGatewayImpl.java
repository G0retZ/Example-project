package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.interactor.SocketGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class SocketGatewayImpl implements SocketGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public SocketGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @Override
  public Completable openSocket() {
    if (stompClient.isConnected()) {
      return Completable.complete();
    }
    Completable completable = stompClient.lifecycle()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .firstElement()
        .flatMapCompletable(lifecycleEvent -> {
          switch (lifecycleEvent.getType()) {
            case OPENED:
              return Completable.complete();
            case ERROR:
              return Completable.error(lifecycleEvent.getException());
            case CLOSED:
              return Completable.error(new ConnectionClosedException());
          }
          return Completable.complete();
        });
    if (!stompClient.isConnecting()) {
      stompClient.connect();
    }
    return completable;
  }

  @Override
  public void closeSocket() {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      stompClient.disconnect();
    }
  }
}
