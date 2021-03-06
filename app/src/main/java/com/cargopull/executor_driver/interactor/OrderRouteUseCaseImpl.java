package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.utils.EmptyEmitter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class OrderRouteUseCaseImpl implements OrderRouteUseCase,
    DataUpdateUseCase<List<RoutePoint>> {

  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final OrderRouteGateway orderRouteGateway;
  @Nullable
  private Flowable<List<RoutePoint>> routeFlowable;
  @NonNull
  private FlowableEmitter<List<RoutePoint>> emitter = new EmptyEmitter<>();

  @Inject
  public OrderRouteUseCaseImpl(@NonNull OrderUseCase orderUseCase,
      @NonNull OrderRouteGateway orderRouteGateway) {
    this.orderUseCase = orderUseCase;
    this.orderRouteGateway = orderRouteGateway;
  }

  @NonNull
  @Override
  public Flowable<List<RoutePoint>> getOrderRoutePoints() {
    if (routeFlowable == null) {
      routeFlowable = Flowable.merge(
          Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER),
          orderUseCase.getOrders()
              .observeOn(Schedulers.single())
              .map(Order::getRoutePath)
              .doOnComplete(() -> emitter.onComplete())
      ).replay(1).refCount();
    }
    return routeFlowable;
  }

  @NonNull
  @Override
  public Completable closeRoutePoint(@NonNull RoutePoint routePoint) {
    return orderRouteGateway.closeRoutePoint(routePoint).observeOn(Schedulers.single())
        .doOnSuccess(emitter::onNext)
        .ignoreElement();
  }

  @NonNull
  @Override
  public Completable nextRoutePoint(@NonNull RoutePoint routePoint) {
    return orderRouteGateway.nextRoutePoint(routePoint).observeOn(Schedulers.single())
        .doOnSuccess(emitter::onNext)
        .ignoreElement();
  }

  @Override
  public void updateWith(@NonNull List<RoutePoint> routePoints) {
    emitter.onNext(routePoints);
  }
}
