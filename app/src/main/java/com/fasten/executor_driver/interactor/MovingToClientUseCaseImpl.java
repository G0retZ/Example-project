package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import javax.inject.Inject;

class MovingToClientUseCaseImpl implements MovingToClientUseCase {

  @NonNull
  private final MovingToClientGateway movingToClientGateway;

  @Inject
  MovingToClientUseCaseImpl(@NonNull MovingToClientGateway movingToClientGateway) {
    this.movingToClientGateway = movingToClientGateway;
  }

  @Override
  public Flowable<Order> getOrders() {
    return movingToClientGateway.getOrders();
  }

  @NonNull
  @Override
  public Completable callToClient() {
    return movingToClientGateway.callToClient();
  }

  @NonNull
  @Override
  public Completable reportArrival() {
    return movingToClientGateway.reportArrival();
  }
}
