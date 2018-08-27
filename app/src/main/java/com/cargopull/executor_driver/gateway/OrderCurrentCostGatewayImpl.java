package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.OrderCurrentCostGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class OrderCurrentCostGatewayImpl implements OrderCurrentCostGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Mapper<StompMessage, Long> mapper;

  @Inject
  public OrderCurrentCostGatewayImpl(@NonNull TopicListener topicListener,
      @NonNull Mapper<StompMessage, Long> mapper) {
    this.topicListener = topicListener;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Long> getOrderCurrentCost() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> stompMessage.findHeader("TotalAmount") != null)
        .map(mapper::map);
  }
}
