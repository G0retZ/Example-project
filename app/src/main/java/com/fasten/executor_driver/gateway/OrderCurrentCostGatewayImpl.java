package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.interactor.OrderCurrentCostGateway;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class OrderCurrentCostGatewayImpl implements OrderCurrentCostGateway {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<StompMessage, Integer> mapper;

  @Inject
  public OrderCurrentCostGatewayImpl(@NonNull StompClient stompClient,
      @NonNull Mapper<StompMessage, Integer> mapper) {
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Integer> getOrderCurrentCost(@NonNull String channelId) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.topic(String.format(BuildConfig.STATUS_DESTINATION, channelId))
          .toFlowable(BackpressureStrategy.BUFFER)
          .subscribeOn(Schedulers.io())
          .onErrorResumeNext(Flowable.empty())
          .filter(stompMessage -> stompMessage.findHeader("TotalAmount") != null)
          .map(mapper::map)
          .observeOn(Schedulers.single());
    }
    return Flowable.error(new ConnectionClosedException());
  }
}
