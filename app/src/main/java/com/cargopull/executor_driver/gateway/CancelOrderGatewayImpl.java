package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.websocket.outgoing.ApiCancelOrderReason;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.interactor.CancelOrderGateway;
import com.google.gson.Gson;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class CancelOrderGatewayImpl implements CancelOrderGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public CancelOrderGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable cancelOrder(@NonNull CancelOrderReason cancelOrderReason) {
    return stompClient.send(BuildConfig.CANCEL_ORDER_DESTINATION,
        new Gson().toJson(new ApiCancelOrderReason(cancelOrderReason)))
        .subscribeOn(Schedulers.io());
  }
}
