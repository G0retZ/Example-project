package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderCurrentCostUseCaseImpl implements OrderCurrentCostUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final OrderCurrentCostGateway orderCurrentCostGateway;

  @Inject
  public OrderCurrentCostUseCaseImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull OrderUseCase orderUseCase,
      @NonNull OrderCurrentCostGateway orderCurrentCostGateway) {
    this.errorReporter = errorReporter;
    this.orderUseCase = orderUseCase;
    this.orderCurrentCostGateway = orderCurrentCostGateway;
  }

  @NonNull
  @Override
  public Flowable<Long> getOrderCurrentCost() {
    return orderUseCase.getOrders()
        .switchMap(order ->
            orderCurrentCostGateway.getOrderCurrentCost()
                .observeOn(Schedulers.single())
                .startWith(order.getTotalCost())
        ).doOnError(errorReporter::reportError);
  }
}
