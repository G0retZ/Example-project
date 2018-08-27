package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderCurrentCostUseCaseImpl implements OrderCurrentCostUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private final OrderCurrentCostGateway orderCurrentCostGateway;

  @Inject
  public OrderCurrentCostUseCaseImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull OrderGateway orderGateway,
      @NonNull DataReceiver<String> loginReceiver,
      @NonNull OrderCurrentCostGateway orderCurrentCostGateway) {
    this.errorReporter = errorReporter;
    this.orderGateway = orderGateway;
    this.loginReceiver = loginReceiver;
    this.orderCurrentCostGateway = orderCurrentCostGateway;
  }

  @NonNull
  @Override
  public Flowable<Long> getOrderCurrentCost() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(login -> orderGateway.getOrders()
            .observeOn(Schedulers.single())
            .switchMap(order ->
                orderCurrentCostGateway.getOrderCurrentCost()
                    .observeOn(Schedulers.single())
                    .startWith(order.getTotalCost())
            )
        ).doOnError(errorReporter::reportError);
  }
}
