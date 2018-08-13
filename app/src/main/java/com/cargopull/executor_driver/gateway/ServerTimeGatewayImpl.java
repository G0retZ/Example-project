package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.interactor.ServerTimeGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class ServerTimeGatewayImpl implements ServerTimeGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public ServerTimeGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Flowable<Long> loadServerTime(@NonNull String channelId) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.topic(
          String.format(BuildConfig.STATUS_DESTINATION, channelId),
          StompClient.ACK_CLIENT_INDIVIDUAL
      ).subscribeOn(Schedulers.io())
          .onErrorResumeNext(Flowable.empty())
          .filter(stompMessage -> stompMessage.findHeader("ServerTimeStamp") != null)
          .map(stompMessage -> {
            try {
              return Long.valueOf(stompMessage.findHeader("ServerTimeStamp"));
            } catch (Throwable t) {
              throw new DataMappingException(t);
            }
          })
          .observeOn(Schedulers.single());
    }
    return Flowable.error(ConnectionClosedException::new);
  }
}