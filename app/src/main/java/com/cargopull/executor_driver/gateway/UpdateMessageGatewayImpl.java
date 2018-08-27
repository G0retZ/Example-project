package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.UpdateMessageGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class UpdateMessageGatewayImpl implements UpdateMessageGateway {

  @NonNull
  private final TopicListener topicListener;

  @Inject
  public UpdateMessageGatewayImpl(@NonNull TopicListener topicListener) {
    this.topicListener = topicListener;
  }

  @NonNull
  @Override
  public Flowable<String> loadUpdateMessages() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> "UpdateVersion".equals(stompMessage.findHeader("message")))
        .map(stompMessage -> stompMessage.getPayload().replace("\"", "").trim());
  }
}
