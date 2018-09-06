package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.utils.ErrorReporter;
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
  @NonNull
  private Flowable<Order> lastOrderCache = Flowable.empty();

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
      orderFlowable = ordersUseCase.getOrdersList()
          .doOnTerminate(() -> lastOrderCache = Flowable.empty())
          .switchMap(orders ->
              publishSubject.toFlowable(BackpressureStrategy.BUFFER)
                  .startWith(lastOrderCache)
                  .map(order -> {
                    if (orders.contains(order)) {
                      return order;
                    }
                    throw new NoSuchElementException();
                  }))
          .doOnError(throwable -> lastOrderCache = Flowable.empty())
          .share();
    }
    return orderFlowable.startWith(lastOrderCache).distinctUntilChanged();
  }

  @Override
  public Completable setSelectedOrder(@NonNull Order order) {
    return ordersUseCase.getOrdersList()
        .firstOrError()
        .flatMapCompletable(orders -> Completable.fromAction(() -> {
          if (!orders.contains(order)) {
            throw new NoSuchElementException("Нет такого заказа в списке");
          }
          lastOrderCache = Flowable.just(order);
          publishSubject.onNext(order);
        })).doOnError(errorReporter::reportError);
  }
}
