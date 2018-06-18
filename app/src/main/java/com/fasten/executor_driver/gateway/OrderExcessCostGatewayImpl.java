package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.OrderExcessCostGateway;
import io.reactivex.Flowable;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class OrderExcessCostGatewayImpl implements OrderExcessCostGateway {

  @SuppressWarnings({"FieldCanBeLocal", "unused"})
  @NonNull
  private final StompClient stompClient;
  @SuppressWarnings({"FieldCanBeLocal", "unused"})
  @NonNull
  private final Mapper<StompMessage, Integer> mapper;

  @Inject
  public OrderExcessCostGatewayImpl(@NonNull StompClient stompClient,
      @NonNull Mapper<StompMessage, Integer> mapper) {
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  // TODO: заглушка из-за неготовности бекенда
  @NonNull
  @Override
  public Flowable<Integer> getOrderExcessCost() {
    return Flowable.never();
  }
}
