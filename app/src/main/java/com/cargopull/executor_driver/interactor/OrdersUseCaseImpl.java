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
  @Nullable
  private Flowable<Set<Order>> ordersFlowable;
  @NonNull
  private final PublishSubject<Order> addSubject = PublishSubject.create();
  @NonNull
  private final PublishSubject<Order> removeSubject = PublishSubject.create();

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
      ordersFlowable = gateway.<Order>getData()
          .observeOn(Schedulers.single())
          .doOnComplete(() -> {
            throw new InterruptedException();
          }).switchMap(
              orders -> addSubject.toFlowable(BackpressureStrategy.BUFFER)
                  .map(order -> addOrderToSet(order, orders))
                  .startWith(orders)
          ).switchMap(
              orders -> removeSubject.toFlowable(BackpressureStrategy.BUFFER)
                  .mergeWith(cancelledOrderUseCase.getOrders())
                  .map(order -> removeOrderFromSet(order, orders))
                  .startWith(orders)
          )
          // Фиксируем сет для неизменяемости далее
          .<Set<Order>>map(HashSet::new)
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

  private Set<Order> addOrderToSet(Order order, Set<Order> orders) {
    // Удаляем старое, если есть
    orders.remove(order);
    // Добавляем то что нужно добавить
    orders.add(order);
    return orders;
  }

  private Set<Order> removeOrderFromSet(Order order, Set<Order> orders) {
    // Удаляем то что нужно удалить
    orders.remove(order);
    return orders;
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
