package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.utils.TimeUtils;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class OrderFulfillmentTimeUseCaseImpl implements OrderFulfillmentTimeUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final TimeUtils timeUtils;

  @Inject
  public OrderFulfillmentTimeUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull TimeUtils timeUtils) {
    this.orderGateway = orderGateway;
    this.timeUtils = timeUtils;
  }

  @NonNull
  @Override
  public Flowable<Long> getOrderElapsedTime() {
    return orderGateway.getOrders()
        .switchMap(order -> {
              long offset =
                  Math.round((timeUtils.currentTimeMillis() - order.getOrderStartTime()) / 1000d);
              return Flowable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
                  .map(count -> count + offset);
            }
        );
  }
}
