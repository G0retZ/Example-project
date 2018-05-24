package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Flowable;

class OrderCurrentCostUseCaseImpl implements OrderCurrentCostUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final OrderCurrentCostGateway orderCurrentCostGateway;

  OrderCurrentCostUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull OrderCurrentCostGateway orderCurrentCostGateway) {
    this.orderGateway = orderGateway;
    this.orderCurrentCostGateway = orderCurrentCostGateway;
  }

  @NonNull
  @Override
  public Flowable<Integer> getOrderCurrentCost() {
    return orderGateway.getOrders(ExecutorState.ORDER_FULFILLMENT)
        .flatMap(
            order -> orderCurrentCostGateway.getOrderCostUpdates()
                .startWith(order.getExcessCost())
                .map(cost -> cost + order.getOrderCost())
        );
  }
}
