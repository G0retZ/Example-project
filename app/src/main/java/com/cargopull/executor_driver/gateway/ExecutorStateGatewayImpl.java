package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class ExecutorStateGatewayImpl implements ExecutorStateGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Mapper<StompMessage, ExecutorState> mapper;

  @Inject
  public ExecutorStateGatewayImpl(@NonNull TopicListener topicListener,
      @NonNull Mapper<StompMessage, ExecutorState> mapper) {
    this.topicListener = topicListener;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<ExecutorState> getState() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> stompMessage.findHeader("Status") != null)
        .map(mapper::map);
  }
}
