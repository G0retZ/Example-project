package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.interactor.OrderGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class OrderGatewayImpl implements OrderGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final List<String> allowedStates = Arrays.asList(
      ExecutorState.DRIVER_ORDER_CONFIRMATION.toString(),
      ExecutorState.CLIENT_ORDER_CONFIRMATION.toString(),
      ExecutorState.MOVING_TO_CLIENT.toString(),
      ExecutorState.WAITING_FOR_CLIENT.toString(),
      ExecutorState.ORDER_FULFILLMENT.toString(),
      ExecutorState.PAYMENT_CONFIRMATION.toString()
  );
  @NonNull
  private final Mapper<StompMessage, Order> mapper;

  @Inject
  public OrderGatewayImpl(
      @NonNull TopicListener topicListener,
      @NonNull Mapper<StompMessage, Order> mapper) {
    this.topicListener = topicListener;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> allowedStates.contains(stompMessage.findHeader("Status")))
        .map(mapper::map);
  }
}
