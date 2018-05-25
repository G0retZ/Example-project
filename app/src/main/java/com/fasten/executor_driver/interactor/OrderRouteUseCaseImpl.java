package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.entity.RoutePoint;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;
import javax.inject.Inject;

public class OrderRouteUseCaseImpl implements OrderRouteUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final OrderRouteGateway orderRouteGateway;

  @Inject
  public OrderRouteUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull OrderRouteGateway orderRouteGateway) {
    this.orderGateway = orderGateway;
    this.orderRouteGateway = orderRouteGateway;
  }

  @NonNull
  @Override
  public Flowable<List<RoutePoint>> getOrderRoutePoints() {
    return orderGateway.getOrders(ExecutorState.ORDER_FULFILLMENT)
        .map(Order::getRoutePath);
  }

  @NonNull
  @Override
  public Completable closeRoutePoint(@NonNull RoutePoint routePoint) {
    return orderRouteGateway.closeRoutePoint(routePoint);
  }

  @NonNull
  @Override
  public Completable completeTheOrder() {
    return orderRouteGateway.completeTheOrder();
  }

  @NonNull
  @Override
  public Completable nextRoutePoint(@NonNull RoutePoint routePoint) {
    return orderRouteGateway.nextRoutePoint(routePoint);
  }
}
