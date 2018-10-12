package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class ConfirmOrderPaymentGatewayImpl implements ConfirmOrderPaymentGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public ConfirmOrderPaymentGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable confirmOrderPayment() {
    return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"COMPLETE_PAYMENT_CONFIRMATION\"")
        .subscribeOn(Schedulers.io());
  }
}
