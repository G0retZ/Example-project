package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderConfirmationUseCaseImpl implements OrderConfirmationUseCase {

  @NonNull
  private final OrderUseCase orderGateway;
  @NonNull
  private final OrderConfirmationGateway orderConfirmationGateway;

  @Inject
  public OrderConfirmationUseCaseImpl(@NonNull OrderUseCase orderGateway,
      @NonNull OrderConfirmationGateway orderConfirmationGateway) {
    this.orderGateway = orderGateway;
    this.orderConfirmationGateway = orderConfirmationGateway;
  }

  @NonNull
  @Override
  public Single<String> sendDecision(boolean confirmed) {
    return orderGateway.getOrders()
        .firstOrError()
        .observeOn(Schedulers.single())
        .flatMap(order -> orderConfirmationGateway.sendDecision(order, confirmed))
        .observeOn(Schedulers.single());
  }
}
