package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class DriverOrderConfirmationUseCaseImpl implements DriverOrderConfirmationUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final OrderConfirmationGateway orderConfirmationGateway;
  @Nullable
  private Order lastOrder;

  @Inject
  public DriverOrderConfirmationUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull OrderConfirmationGateway orderConfirmationGateway) {
    this.orderGateway = orderGateway;
    this.orderConfirmationGateway = orderConfirmationGateway;
  }

  @Override
  public Flowable<Order> getOrders() {
    return orderGateway.getOrders()
        .doOnNext(order -> lastOrder = order);
  }

  @NonNull
  @Override
  public Completable sendDecision(boolean confirmed) {
    if (lastOrder == null) {
      return Completable.error(new NoOrdersAvailableException());
    }
    return orderConfirmationGateway.sendDecision(lastOrder, confirmed);
  }
}
