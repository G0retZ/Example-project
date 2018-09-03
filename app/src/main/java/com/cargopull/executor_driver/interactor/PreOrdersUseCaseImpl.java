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

class PreOrdersUseCaseImpl implements PreOrdersUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CommonGateway<List<Order>> gateway;
  // Это для того чтобы combineLatest стартовал сразу.
  @NonNull
  private final Order dumbOrder = new Order(0, "", "", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0);
  @Nullable
  private Flowable<List<Order>> preOrderFlowable;
  @NonNull
  private Emitter<Order> scheduleEmitter = new Emitter<Order>() {
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
  private Emitter<Order> unScheduleEmitter = new Emitter<Order>() {
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

  PreOrdersUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CommonGateway<List<Order>> gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<List<Order>> getPreOrders() {
    if (preOrderFlowable == null) {
      preOrderFlowable = Flowable.combineLatest(
          Flowable.<Order>create(
              emitter -> this.scheduleEmitter = emitter,
              BackpressureStrategy.BUFFER
          ).startWith(dumbOrder),
          Flowable.<Order>create(
              emitter -> this.unScheduleEmitter = emitter,
              BackpressureStrategy.BUFFER
          ).startWith(dumbOrder),
          gateway.<Order>getData()
              .observeOn(Schedulers.single())
              .doOnComplete(() -> {
                scheduleEmitter.onComplete();
                unScheduleEmitter.onComplete();
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
    return preOrderFlowable;
  }

  @Override
  public void unSchedulePreOrder(@NonNull Order order) {
    unScheduleEmitter.onNext(order);
  }

  @Override
  public void schedulePreOrder(@NonNull Order order) {
    scheduleEmitter.onNext(order);
  }
}
