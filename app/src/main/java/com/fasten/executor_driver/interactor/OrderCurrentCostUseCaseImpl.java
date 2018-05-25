package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
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
    return orderGateway.getOrders(ExecutorState.ORDER_FULFILLMENT)
        .switchMap(
            order -> orderExcessCostGateway.getOrderExcessCost()
                .startWith(order.getExcessCost())
                .map(cost -> cost + order.getOrderCost())
        );
  }
}
