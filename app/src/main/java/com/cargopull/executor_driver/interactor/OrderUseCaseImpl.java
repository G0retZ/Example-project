package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderUseCaseImpl implements OrderUseCase, OrderDecisionUseCase, OrderUpdateUseCase {

  @NonNull
  private final CommonGateway<Order> gateway;
  @Nullable
  private Flowable<Order> orderFlowable;
  @NonNull
  private Emitter<Order> emitter = new Emitter<Order>() {
    @Override
    public void onNext(Order value) {
    }

    @Override
    public void onError(Throwable error) {
    }

    @Override
    public void onComplete() {
    }
  };

  @Inject
  public OrderUseCaseImpl(@NonNull CommonGateway<Order> gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    if (orderFlowable == null) {
      orderFlowable = Flowable.merge(
          Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER),
          gateway.getData()
              .observeOn(Schedulers.single())
              .doOnComplete(() -> emitter.onComplete())
      ).replay(1)
          .refCount();
    }
    return orderFlowable;
  }

  @Override
  public void setOrderOfferDecisionMade() {
    emitter.onError(new OrderOfferDecisionException());
  }

  @Override
  public void updateOrderWith(@NonNull Order order) {
    emitter.onNext(order);
  }
}
