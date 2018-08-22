package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.interactor.ExecutorBalanceGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class ExecutorBalanceGatewayImpl implements ExecutorBalanceGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Mapper<StompMessage, ExecutorBalance> mapper;

  @Inject
  public ExecutorBalanceGatewayImpl(@NonNull TopicListener topicListener,
      @NonNull Mapper<StompMessage, ExecutorBalance> mapper) {
    this.topicListener = topicListener;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<ExecutorBalance> loadExecutorBalance() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> stompMessage.findHeader("Balance") != null)
        .map(mapper::map);
  }
}
