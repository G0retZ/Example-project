package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class OrderUseCaseImpl implements OrderUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderGateway orderGateway;

  @Inject
  public OrderUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull OrderGateway orderGateway) {
    this.errorReporter = errorReporter;
    this.orderGateway = orderGateway;
  }

  @Override
  public Flowable<Order> getOrders() {
    return orderGateway.getOrders()
        .doOnError(errorReporter::reportError);
  }
}
