package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class OrderRouteUseCaseImpl implements OrderRouteUseCase {

  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final OrderRouteGateway orderRouteGateway;

  @Inject
  public OrderRouteUseCaseImpl(@NonNull OrderUseCase orderUseCase,
      @NonNull OrderRouteGateway orderRouteGateway) {
    this.orderUseCase = orderUseCase;
    this.orderRouteGateway = orderRouteGateway;
  }

  @NonNull
  @Override
  public Flowable<List<RoutePoint>> getOrderRoutePoints() {
    return orderUseCase.getOrders().map(Order::getRoutePath);
  }

  @NonNull
  @Override
  public Completable closeRoutePoint(@NonNull RoutePoint routePoint) {
    return orderRouteGateway.closeRoutePoint(routePoint).observeOn(Schedulers.single());
  }

  @NonNull
  @Override
  public Completable completeTheOrder() {
    return orderRouteGateway.completeTheOrder().observeOn(Schedulers.single());
  }

  @NonNull
  @Override
  public Completable nextRoutePoint(@NonNull RoutePoint routePoint) {
    return orderRouteGateway.nextRoutePoint(routePoint).observeOn(Schedulers.single());
  }
}
