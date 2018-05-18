package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.interactor.OrderConfirmationGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class OrderConfirmationGatewayImpl implements OrderConfirmationGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public OrderConfirmationGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable sendDecision(@NonNull Order order, boolean accepted) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(
          BuildConfig.CONFIRM_OFFER_DESTINATION,
          "{\"id\":\"" + order.getId() + "\", \"approved\":\"" + accepted + "\"}"
      )
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }
}
