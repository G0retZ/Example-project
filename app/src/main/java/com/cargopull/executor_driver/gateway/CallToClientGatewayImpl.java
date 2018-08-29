package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.interactor.CallToClientGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class CallToClientGatewayImpl implements CallToClientGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public CallToClientGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable callToClient() {
    return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"CALL_TO_CLIENT\"")
        .subscribeOn(Schedulers.io());
  }
}
