package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderCurrentCostUseCaseImpl implements OrderCurrentCostUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final CommonGateway<Long> orderCurrentCostGateway;

  @Inject
  public OrderCurrentCostUseCaseImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull OrderUseCase orderUseCase,
      @NonNull CommonGateway<Long> orderCurrentCostGateway) {
    this.errorReporter = errorReporter;
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
        ).doOnError(errorReporter::reportError);
  }
}
