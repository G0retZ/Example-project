package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import javax.inject.Inject;
import ua.naiksoftware.stomp.StompHeader;
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
      return stompClient.topic(
          String.format(BuildConfig.STATUS_DESTINATION, channelId),
          StompClient.ACK_CLIENT_INDIVIDUAL
      ).subscribeOn(Schedulers.io())
          .onErrorResumeNext(Flowable.empty())
          .filter(stompMessage -> stompMessage.findHeader("Status") != null)
          .doOnNext(
              stompMessage -> stompClient.sendAfterConnection(
                  new StompMessage("ACK",
                      Arrays.asList(
                          new StompHeader("subscription", stompMessage.findHeader("subscription")),
                          new StompHeader("message-id", stompMessage.findHeader("message-id"))
                      ),
                      ""
                  )
              ).subscribe(() -> {
              }, Throwable::printStackTrace)
          )
          .map(mapper::map)
          .observeOn(Schedulers.single());
    }
    return Flowable.error(new ConnectionClosedException());
  }
}
