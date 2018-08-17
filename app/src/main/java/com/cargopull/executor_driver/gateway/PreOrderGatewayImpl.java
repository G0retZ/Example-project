package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.interactor.OrderGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import javax.inject.Inject;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class PreOrderGatewayImpl implements OrderGateway {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<String, Order> mapper;

  @Inject
  public PreOrderGatewayImpl(
      @NonNull StompClient stompClient,
      @NonNull Mapper<String, Order> mapper) {
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders(@Nullable String channelId) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.topic(
          String.format(BuildConfig.STATUS_DESTINATION, channelId),
          StompClient.ACK_CLIENT_INDIVIDUAL
      ).subscribeOn(Schedulers.io())
          .onErrorResumeNext(Flowable.empty())
          .filter(stompMessage -> stompMessage.findHeader("Preliminary") != null)
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
          .map(from -> {
            if (from.getPayload() == null) {
              throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
            }
            return mapper.map(from.getPayload());
          })
          .observeOn(Schedulers.single());
    }
    return Flowable.error(ConnectionClosedException::new);
  }
}
