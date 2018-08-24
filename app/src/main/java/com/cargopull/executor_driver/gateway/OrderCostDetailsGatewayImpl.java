package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.interactor.OrderCostDetailsGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class OrderCostDetailsGatewayImpl implements OrderCostDetailsGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Mapper<StompMessage, OrderCostDetails> mapper;

  @Inject
  public OrderCostDetailsGatewayImpl(@NonNull TopicListener topicListener,
      @NonNull Mapper<StompMessage, OrderCostDetails> mapper) {
    this.topicListener = topicListener;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<OrderCostDetails> getOrderCostDetails() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> "PAYMENT_CONFIRMATION".equals(stompMessage.findHeader("Status")))
        .map(mapper::map);
  }
}
