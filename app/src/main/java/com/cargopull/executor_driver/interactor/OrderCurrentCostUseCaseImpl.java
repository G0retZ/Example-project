package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderCurrentCostUseCaseImpl implements OrderCurrentCostUseCase {

  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final CommonGateway<Long> orderCurrentCostGateway;

  @Inject
  public OrderCurrentCostUseCaseImpl(
      @NonNull OrderUseCase orderUseCase,
      @NonNull CommonGateway<Long> orderCurrentCostGateway) {
    this.orderUseCase = orderUseCase;
    this.orderCurrentCostGateway = orderCurrentCostGateway;
  }

  @NonNull
  @Override
  public Flowable<Long> getOrderCurrentCost() {
    return orderUseCase.getOrders()
        .switchMap(order ->
            orderCurrentCostGateway.getData()
                .observeOn(Schedulers.single())
                .startWith(order.getTotalCost())
        );
  }
}
