package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class OrderUseCaseImpl implements OrderUseCase {

  @NonNull
  private final OrderGateway orderGateway;

  @Inject
  public OrderUseCaseImpl(@NonNull OrderGateway orderGateway) {
    this.orderGateway = orderGateway;
  }

  @Override
  public Flowable<Order> getOrders() {
    return orderGateway.getOrders();
  }
}
