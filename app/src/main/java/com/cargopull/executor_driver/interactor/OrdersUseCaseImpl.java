package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.HashSet;
import java.util.Set;

public class OrdersUseCaseImpl implements OrdersUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CommonGateway<Set<Order>> gateway;
  @NonNull
  private final OrderUseCase cancelledOrderUseCase;
  // Это для того чтобы combineLatest стартовал сразу.
  @NonNull
  private final Order dumbOrder = new Order(-1, "", "", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0);
  @Nullable
  private Flowable<Set<Order>> ordersFlowable;
  @NonNull
  private PublishSubject<Order> addSubject = PublishSubject.create();
  @NonNull
  private PublishSubject<Order> removeSubject = PublishSubject.create();

  public OrdersUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CommonGateway<Set<Order>> gateway,
      @NonNull OrderUseCase cancelledOrderUseCase) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
    this.cancelledOrderUseCase = cancelledOrderUseCase;
  }

  @NonNull
  @Override
  public Flowable<Set<Order>> getOrdersSet() {
    if (ordersFlowable == null) {
      ordersFlowable = Flowable.combineLatest(
          addSubject.toFlowable(BackpressureStrategy.BUFFER)
              .startWith(dumbOrder),
          removeSubject.toFlowable(BackpressureStrategy.BUFFER)
              .startWith(dumbOrder)
              .mergeWith(cancelledOrderUseCase.getOrders()),
          gateway.<Order>getData()
              .observeOn(Schedulers.single())
              .doOnComplete(() -> {
                throw new InterruptedException();
              }),
          this::merge
      )
          .onErrorResumeNext(throwable -> {
            if (throwable instanceof InterruptedException) {
              return Flowable.empty();
            }
            return Flowable.error(throwable);
          })
          .doOnError(errorReporter::reportError)
          .replay(1)
          .refCount();
    }
    return ordersFlowable;
  }

  private Set<Order> merge(Order addPreOrder, Order removePreOrder, Set<Order> preOrders) {
    // Удаляем старое и то что нужно удалить
    preOrders.remove(addPreOrder);
    preOrders.remove(removePreOrder);
    // Добавляем то что нужно добавить, если это не заказ-заглушка
    if (addPreOrder != dumbOrder) {
      preOrders.add(addPreOrder);
    }
    return new HashSet<>(preOrders);
  }

  @Override
  public void addOrder(@NonNull Order order) {
    addSubject.onNext(order);
  }

  @Override
  public void removeOrder(@NonNull Order order) {
    removeSubject.onNext(order);
  }
}
