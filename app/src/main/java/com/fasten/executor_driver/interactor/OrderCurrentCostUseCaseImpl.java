package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class OrderCurrentCostUseCaseImpl implements OrderCurrentCostUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private final OrderCurrentCostGateway orderCurrentCostGateway;

  @Inject
  public OrderCurrentCostUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull DataReceiver<String> loginReceiver,
      @NonNull OrderCurrentCostGateway orderCurrentCostGateway) {
    this.orderGateway = orderGateway;
    this.loginReceiver = loginReceiver;
    this.orderCurrentCostGateway = orderCurrentCostGateway;
  }

  @NonNull
  @Override
  public Flowable<Integer> getOrderCurrentCost() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(login -> orderGateway.getOrders()
            .switchMap(order ->
                orderCurrentCostGateway.getOrderCurrentCost(login)
                    .startWith(order.getTotalCost())
            )
        );
  }
}
