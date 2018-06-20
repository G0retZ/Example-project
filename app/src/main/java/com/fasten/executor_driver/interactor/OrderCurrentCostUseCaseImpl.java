package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class OrderCurrentCostUseCaseImpl implements OrderCurrentCostUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final OrderExcessCostGateway orderExcessCostGateway;

  @Inject
  public OrderCurrentCostUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull OrderExcessCostGateway orderExcessCostGateway) {
    this.orderGateway = orderGateway;
    this.orderExcessCostGateway = orderExcessCostGateway;
  }

  @NonNull
  @Override
  public Flowable<Integer> getOrderCurrentCost() {
    return orderGateway.getOrders()
        .switchMap(
            order -> orderExcessCostGateway.getOrderExcessCost()
                .startWith(order.getTotalCost())
        );
  }
}
