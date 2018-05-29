package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class MovingToClientUseCaseImpl implements MovingToClientUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final MovingToClientGateway movingToClientGateway;

  @Inject
  public MovingToClientUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull MovingToClientGateway movingToClientGateway) {
    this.orderGateway = orderGateway;
    this.movingToClientGateway = movingToClientGateway;
  }

  @Override
  public Flowable<Order> getOrders() {
    return orderGateway.getOrders(ExecutorState.MOVING_TO_CLIENT);
  }

  @NonNull
  @Override
  public Completable reportArrival() {
    return movingToClientGateway.reportArrival();
  }
}
