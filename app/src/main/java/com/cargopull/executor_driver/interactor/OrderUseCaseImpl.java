package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class OrderUseCaseImpl implements OrderUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  @Inject
  public OrderUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull OrderGateway orderGateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.errorReporter = errorReporter;
    this.orderGateway = orderGateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(orderGateway::getOrders)
        .doOnError(errorReporter::reportError);
  }
}
