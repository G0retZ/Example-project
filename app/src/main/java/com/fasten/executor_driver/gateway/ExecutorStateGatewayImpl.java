package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.interactor.ExecutorStateGateway;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class ExecutorStateGatewayImpl implements ExecutorStateGateway {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<StompMessage, ExecutorState> mapper;

  @Inject
  public ExecutorStateGatewayImpl(@NonNull StompClient stompClient,
      @NonNull Mapper<StompMessage, ExecutorState> mapper) {
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<ExecutorState> getState(@Nullable String channelId) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.topic(String.format(BuildConfig.STATS_DESTINATION, channelId))
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single())
          .filter(stompMessage -> stompMessage.findHeader("Status") != null || (
              stompMessage.findHeader("Type") != null
                  && stompMessage.findHeader("Type").equals("Status")))
          .map(mapper::map)
          .toFlowable(BackpressureStrategy.BUFFER);
    }
    return Flowable.error(new ConnectionClosedException());
  }
}
