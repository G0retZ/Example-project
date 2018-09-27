package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class OrderFulfillmentTimeUseCaseImpl implements OrderFulfillmentTimeUseCase {

  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final TimeUtils timeUtils;

  @Inject
  public OrderFulfillmentTimeUseCaseImpl(@NonNull OrderUseCase orderUseCase,
      @NonNull TimeUtils timeUtils) {
    this.orderUseCase = orderUseCase;
    this.timeUtils = timeUtils;
  }

  @NonNull
  @Override
  public Flowable<Long> getOrderElapsedTime() {
    return orderUseCase.getOrders()
        .switchMap(order -> {
              long offset =
                  Math.round((timeUtils.currentTimeMillis() - order.getStartTime()) / 1000d);
              return Flowable.interval(0, 1, TimeUnit.SECONDS)
                  .map(count -> count + offset)
                  .observeOn(Schedulers.single());
            }
        );
  }
}
