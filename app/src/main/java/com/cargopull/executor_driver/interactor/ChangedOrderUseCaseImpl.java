package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ChangedOrderUseCaseImpl implements OrderUseCase {

  @NonNull
  private final CommonGateway<Order> gateway;
  @Nullable
  private Flowable<Order> orderFlowable;

  @Inject
  public ChangedOrderUseCaseImpl(@NonNull CommonGateway<Order> gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    if (orderFlowable == null) {
      orderFlowable = gateway.getData().observeOn(Schedulers.single()).share();
    }
    return orderFlowable;
  }
}
