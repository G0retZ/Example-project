package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class OrdersUseCaseImpl implements OrdersUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CommonGateway<List<Order>> gateway;
  // Это для того чтобы combineLatest стартовал сразу.
  @NonNull
  private final Order dumbOrder = new Order(0, "", "", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0);
  @Nullable
  private Flowable<List<Order>> ordersFlowable;
  @NonNull
  private Emitter<Order> addEmitter = new Emitter<Order>() {
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
  @NonNull
  private Emitter<Order> removeEmitter = new Emitter<Order>() {
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

  public OrdersUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CommonGateway<List<Order>> gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<List<Order>> getOrdersList() {
    if (ordersFlowable == null) {
      ordersFlowable = Flowable.combineLatest(
          Flowable.<Order>create(
              emitter -> this.addEmitter = emitter,
              BackpressureStrategy.BUFFER
          ).startWith(dumbOrder),
          Flowable.<Order>create(
              emitter -> this.removeEmitter = emitter,
              BackpressureStrategy.BUFFER
          ).startWith(dumbOrder),
          gateway.<Order>getData()
              .observeOn(Schedulers.single())
              .doOnComplete(() -> {
                addEmitter.onComplete();
                removeEmitter.onComplete();
              }),
          (scheduledOrder, unScheduledOrder, preOrders) -> {
            preOrders = new ArrayList<>(preOrders);
            if (scheduledOrder != dumbOrder && !preOrders.contains(scheduledOrder)) {
              preOrders.add(scheduledOrder);
            }
            preOrders.remove(unScheduledOrder);
            return preOrders;
          }
      )
          .doOnError(errorReporter::reportError)
          .replay(1)
          .refCount();
    }
    return ordersFlowable;
  }

  @Override
  public void addOrder(@NonNull Order order) {
    addEmitter.onNext(order);
  }

  @Override
  public void removeOrder(@NonNull Order order) {
    removeEmitter.onNext(order);
  }
}
