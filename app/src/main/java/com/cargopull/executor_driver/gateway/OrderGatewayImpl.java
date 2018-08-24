package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.interactor.OrderGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class OrderGatewayImpl implements OrderGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final ExecutorState executorState;
  @NonNull
  private final Mapper<String, Order> mapper;

  @Inject
  public OrderGatewayImpl(
      @NonNull TopicListener topicListener,
      @NonNull ExecutorState executorState,
      @NonNull Mapper<String, Order> mapper) {
    this.topicListener = topicListener;
    this.executorState = executorState;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> executorState.toString().equals(stompMessage.findHeader("Status")))
        .map((StompMessage from) -> mapper.map(from.getPayload()));
  }
}
