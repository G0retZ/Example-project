package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Flowable;
import java.util.Set;
import javax.inject.Inject;

/**
 * Удаляет заказ из списка при любом действии
 */
public class RemoveOrdersUseCaseImpl implements OrdersUseCase {

  @NonNull
  private final OrdersUseCase ordersUseCase;

  @Inject
  public RemoveOrdersUseCaseImpl(@NonNull OrdersUseCase ordersUseCase) {
    this.ordersUseCase = ordersUseCase;
  }

  @NonNull
  @Override
  public Flowable<Set<Order>> getOrdersSet() {
    return ordersUseCase.getOrdersSet();
  }

  @Override
  public void addOrder(@NonNull Order order) {
    ordersUseCase.removeOrder(order);
  }

  @Override
  public void removeOrder(@NonNull Order order) {
    ordersUseCase.removeOrder(order);
  }
}
