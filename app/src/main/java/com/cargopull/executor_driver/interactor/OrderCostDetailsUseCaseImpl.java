package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.utils.ErrorReporter;
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
