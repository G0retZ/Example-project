package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.interactor.CurrentCostPollingGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.client.StompClient;

public class CurrentCostPollingGatewayImpl implements CurrentCostPollingGateway {

  @NonNull
  private final StompClient stompClient;

  public CurrentCostPollingGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable poll() {
    return stompClient.send(BuildConfig.POLLING_DESTINATION, "\"\"")
        .subscribeOn(Schedulers.io());
  }
}
