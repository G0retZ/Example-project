package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Single;
import javax.inject.Inject;

public class OrderConfirmationUseCaseImpl implements OrderConfirmationUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final OrderConfirmationGateway orderConfirmationGateway;

  @Inject
  public OrderConfirmationUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull OrderConfirmationGateway orderConfirmationGateway) {
    this.orderGateway = orderGateway;
    this.orderConfirmationGateway = orderConfirmationGateway;
  }

  @NonNull
  @Override
  public Single<String> sendDecision(boolean confirmed) {
    return orderGateway.getOrders().firstOrError()
        .flatMap(order -> orderConfirmationGateway.sendDecision(order, confirmed));
  }
}
