package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.interactor.OrderExcessCostGateway;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class OrderExcessCostGatewayImpl implements OrderExcessCostGateway {

  @NonNull
  private final StompClient stompClient;
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
