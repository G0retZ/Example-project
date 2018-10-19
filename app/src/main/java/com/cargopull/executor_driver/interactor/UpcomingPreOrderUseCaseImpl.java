package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class UpcomingPreOrderUseCaseImpl implements OrderUseCase {

  @NonNull
  private final CommonGateway<Order> gateway;
  @NonNull
  private final OrdersUseCase ordersUseCase;
  @Nullable
  private Flowable<Order> orderFlowable;

  @Inject
  public UpcomingPreOrderUseCaseImpl(
      @NonNull CommonGateway<Order> gateway,
      @NonNull OrdersUseCase ordersUseCase) {
    this.gateway = gateway;
    this.ordersUseCase = ordersUseCase;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    if (orderFlowable == null) {
      orderFlowable = gateway.getData()
          .observeOn(Schedulers.single())
          .doOnComplete(() -> {
            throw new InterruptedException();
          })
          .switchMap(order ->
              ordersUseCase.getOrdersSet()
                  .map(orders -> {
                    for (Order preOrder : orders) {
                      if (order.equals(preOrder)) {
                        return preOrder.withEtaToStartPoint(order.getEtaToStartPoint());
                      }
                    }
                    throw new OrderCancelledException();
                  }))
          .onErrorResumeNext(throwable -> {
            if (throwable instanceof InterruptedException) {
              return Flowable.empty();
            }
            return Flowable.error(throwable);
          })
          .replay(1)
          .refCount();
    }
    return orderFlowable;
  }
}
