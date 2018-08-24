package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class OrderRouteUseCaseImpl implements OrderRouteUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private final OrderRouteGateway orderRouteGateway;

  @Inject
  public OrderRouteUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull OrderGateway orderGateway,
      @NonNull DataReceiver<String> loginReceiver,
      @NonNull OrderRouteGateway orderRouteGateway) {
    this.errorReporter = errorReporter;
    this.orderGateway = orderGateway;
    this.loginReceiver = loginReceiver;
    this.orderRouteGateway = orderRouteGateway;
  }

  @NonNull
  @Override
  public Flowable<List<RoutePoint>> getOrderRoutePoints() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(login -> orderGateway.getOrders())
        .observeOn(Schedulers.single())
        .map(Order::getRoutePath)
        .doOnError(errorReporter::reportError);
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
