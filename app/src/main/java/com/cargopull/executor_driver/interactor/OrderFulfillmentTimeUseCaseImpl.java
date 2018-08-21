package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.ErrorReporter;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class OrderFulfillmentTimeUseCaseImpl implements OrderFulfillmentTimeUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private final TimeUtils timeUtils;

  @Inject
  public OrderFulfillmentTimeUseCaseImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull OrderGateway orderGateway,
      @NonNull DataReceiver<String> loginReceiver,
      @NonNull TimeUtils timeUtils) {
    this.errorReporter = errorReporter;
    this.orderGateway = orderGateway;
    this.loginReceiver = loginReceiver;
    this.timeUtils = timeUtils;
  }

  @NonNull
  @Override
  public Flowable<Long> getOrderElapsedTime() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(orderGateway::getOrders)
        .observeOn(Schedulers.single())
        .switchMap(order -> {
              long offset =
                  Math.round((timeUtils.currentTimeMillis() - order.getStartTime()) / 1000d);
              return Flowable.interval(0, 1, TimeUnit.SECONDS)
                  .map(count -> count + offset)
                  .observeOn(Schedulers.single());
            }
        ).doOnError(errorReporter::reportError);
  }
}
