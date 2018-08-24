package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.interactor.OrderGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class PreOrderGatewayImpl implements OrderGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Mapper<String, Order> mapper;

  @Inject
  public PreOrderGatewayImpl(@NonNull TopicListener topicListener,
      @NonNull Mapper<String, Order> mapper) {
    this.topicListener = topicListener;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> stompMessage.findHeader("Preliminary") != null)
        .map(from -> mapper.map(from.getPayload()));
  }
}
