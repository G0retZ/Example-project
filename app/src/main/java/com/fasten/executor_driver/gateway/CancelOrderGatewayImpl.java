package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.backend.websocket.outgoing.ApiCancelOrderReason;
import com.fasten.executor_driver.entity.CancelOrderReason;
import com.fasten.executor_driver.interactor.CancelOrderGateway;
import com.google.gson.Gson;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class CancelOrderGatewayImpl implements CancelOrderGateway {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<StompMessage, List<CancelOrderReason>> mapper;

  @Inject
  public CancelOrderGatewayImpl(@NonNull StompClient stompClient,
      @NonNull Mapper<StompMessage, List<CancelOrderReason>> mapper) {
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<List<CancelOrderReason>> loadCancelOrderReasons(@Nullable String channelId) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.topic(
          String.format(BuildConfig.STATUS_DESTINATION, channelId),
          StompClient.ACK_CLIENT_INDIVIDUAL
      ).subscribeOn(Schedulers.io())
          .onErrorResumeNext(Flowable.empty())
          .filter(stompMessage -> stompMessage.findHeader("CancelReason") != null)
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

  @NonNull
  @Override
  public Completable cancelOrder(@NonNull CancelOrderReason cancelOrderReason) {
    return stompClient.send(BuildConfig.CANCEL_ORDER_DESTINATION,
        new Gson().toJson(new ApiCancelOrderReason(cancelOrderReason)))
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
