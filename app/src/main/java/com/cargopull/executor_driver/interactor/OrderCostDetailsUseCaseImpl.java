package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class OrderCostDetailsUseCaseImpl implements OrderCostDetailsUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CommonGateway<OrderCostDetails> orderCostDetailsGateway;

  public OrderCostDetailsUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CommonGateway<OrderCostDetails> orderCostDetailsGateway) {
    this.errorReporter = errorReporter;
    this.orderCostDetailsGateway = orderCostDetailsGateway;
  }

  @Override
  public Flowable<OrderCostDetails> getOrderCostDetails() {
    return orderCostDetailsGateway.getData()
        .observeOn(Schedulers.single())
        .doOnError(errorReporter::reportError);
  }
}
