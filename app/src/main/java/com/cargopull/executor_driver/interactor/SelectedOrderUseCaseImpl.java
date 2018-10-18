package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;
import java.util.NoSuchElementException;
import javax.inject.Inject;

public class SelectedOrderUseCaseImpl implements OrderUseCase, SelectedOrderUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrdersUseCase ordersUseCase;
  @NonNull
  private final PublishSubject<Order> publishSubject;
  @Nullable
  private Flowable<Order> orderFlowable;
  @Nullable
  private Order lastOrder;

  @Inject
  public SelectedOrderUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull OrdersUseCase ordersUseCase) {
    this.errorReporter = errorReporter;
    this.ordersUseCase = ordersUseCase;
    publishSubject = PublishSubject.create();
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    if (orderFlowable == null) {
      orderFlowable = ordersUseCase.getOrdersSet()
          .doOnComplete(() -> {
            throw new InterruptedException();
          })
          .switchMap(orders ->
              publishSubject.toFlowable(BackpressureStrategy.BUFFER)
                  .startWith(Flowable.create(e -> {
                    if (lastOrder != null) {
                      e.onNext(lastOrder);
                    }
                    e.onComplete();
                  }, BackpressureStrategy.BUFFER))
                  .map(order -> {
                    if (orders.contains(order)) {
                      return order;
                    }
                    throw new OrderCancelledException();
                  })).distinctUntilChanged()
          .onErrorResumeNext(throwable -> {
            if (throwable instanceof InterruptedException) {
              return Flowable.empty();
            }
            return Flowable.error(throwable);
          })
          .doOnTerminate(() -> lastOrder = null)
          .replay(1)
          .refCount();
    }
    return orderFlowable;
  }

  @Override
  public Completable setSelectedOrder(@NonNull Order order) {
    return ordersUseCase.getOrdersSet()
        .firstOrError()
        .flatMapCompletable(orders -> Completable.fromAction(() -> {
          if (!orders.contains(order)) {
            throw new NoSuchElementException("Нет такого заказа в списке");
          }
          lastOrder = order;
          publishSubject.onNext(order);
        })).doOnError(errorReporter::reportError);
  }
}
