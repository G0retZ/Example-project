package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.MissedOrderGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class MissedOrderGatewayImpl implements MissedOrderGateway {

  @NonNull
  private final TopicListener topicListener;

  @Inject
  public MissedOrderGatewayImpl(@NonNull TopicListener topicListener) {
    this.topicListener = topicListener;
  }

  @NonNull
  @Override
  public Flowable<String> loadMissedOrdersMessages() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> stompMessage.findHeader("MissedOrder") != null)
        .map(stompMessage -> stompMessage.getPayload().trim());
  }
}
