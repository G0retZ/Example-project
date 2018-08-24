package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderConfirmationUseCaseImpl implements OrderConfirmationUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final OrderConfirmationGateway orderConfirmationGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  @Inject
  public OrderConfirmationUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull OrderConfirmationGateway orderConfirmationGateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.orderGateway = orderGateway;
    this.orderConfirmationGateway = orderConfirmationGateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Single<String> sendDecision(boolean confirmed) {
    return loginReceiver.get()
        .firstOrError()
        .flatMapPublisher(login -> orderGateway.getOrders())
        .firstOrError()
        .observeOn(Schedulers.single())
        .flatMap(order -> orderConfirmationGateway.sendDecision(order, confirmed))
        .observeOn(Schedulers.single());
  }
}
