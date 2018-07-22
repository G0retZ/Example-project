package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;

class OrderCostDetailsCaseImpl implements OrderCostDetailsCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderCostDetailsGateway orderCostDetailsGateway;

  OrderCostDetailsCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull OrderCostDetailsGateway orderCostDetailsGateway) {
    this.errorReporter = errorReporter;
    this.orderCostDetailsGateway = orderCostDetailsGateway;
  }

  @Override
  public Flowable<OrderCostDetails> getOrderCostDetails() {
    return orderCostDetailsGateway.getOrderCostDetails()
        .doOnError(errorReporter::reportError);
  }
}
