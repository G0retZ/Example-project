package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.ServerTimeGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ServerTimeGatewayImpl implements ServerTimeGateway {

  @NonNull
  private final TopicListener topicListener;

  @Inject
  public ServerTimeGatewayImpl(@NonNull TopicListener topicListener) {
    this.topicListener = topicListener;
  }

  @NonNull
  @Override
  public Flowable<Long> loadServerTime() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> stompMessage.findHeader("ServerTimeStamp") != null)
        .map(stompMessage -> {
          try {
            return Long.valueOf(stompMessage.findHeader("ServerTimeStamp"));
          } catch (Throwable t) {
            throw new DataMappingException(t);
          }
        });
  }
}
