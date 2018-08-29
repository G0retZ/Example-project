package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.interactor.WaitingForClientGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class WaitingForClientGatewayImpl implements WaitingForClientGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public WaitingForClientGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable startTheOrder() {
    return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"START_ORDER\"")
        .subscribeOn(Schedulers.io());
  }
}
