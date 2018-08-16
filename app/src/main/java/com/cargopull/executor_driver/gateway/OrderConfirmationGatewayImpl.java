package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway;
import io.reactivex.Single;
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
  public Single<String> sendDecision(@NonNull Order order, boolean accepted) {
    return stompClient.send(
        BuildConfig.CONFIRM_OFFER_DESTINATION,
        "{\"id\":\"" + order.getId() + "\", \"approved\":\"" + accepted + "\"}"
    ).toSingle(() -> "")
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
