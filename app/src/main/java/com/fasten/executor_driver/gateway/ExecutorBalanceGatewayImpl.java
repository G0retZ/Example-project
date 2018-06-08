package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorBalance;
import com.fasten.executor_driver.interactor.ExecutorBalanceGateway;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class ExecutorBalanceGatewayImpl implements ExecutorBalanceGateway {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<StompMessage, ExecutorBalance> mapper;

  @Inject
  public ExecutorBalanceGatewayImpl(@NonNull StompClient stompClient,
      @NonNull Mapper<StompMessage, ExecutorBalance> mapper) {
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<ExecutorBalance> loadExecutorBalance(@NonNull String channelId) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.topic(String.format(BuildConfig.STATUS_DESTINATION, channelId))
          .toFlowable(BackpressureStrategy.BUFFER)
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single())
          .filter(stompMessage -> stompMessage.findHeader("Balance") != null)
          .map(mapper::map);
    }
    return Flowable.error(new ConnectionClosedException());
  }
}
