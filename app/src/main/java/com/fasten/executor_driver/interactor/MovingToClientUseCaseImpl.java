package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import javax.inject.Inject;

class MovingToClientUseCaseImpl implements MovingToClientUseCase {

  @NonNull
  private final MovingToClientGateway movingToClientGateway;
  @Nullable
  private Order lastOrder;

  @Inject
  MovingToClientUseCaseImpl(@NonNull MovingToClientGateway movingToClientGateway) {
    this.movingToClientGateway = movingToClientGateway;
  }

  @Override
  public Flowable<Order> getOrders() {
    return movingToClientGateway.getOrders().doOnNext(order -> lastOrder = order);
  }

  @NonNull
  @Override
  public Completable callToClient() {
    if (lastOrder == null) {
      return Completable.error(new NoOrdersAvailableException());
    }
    return movingToClientGateway.callToClient(lastOrder);
  }

  @NonNull
  @Override
  public Completable reportArrival() {
    if (lastOrder == null) {
      return Completable.error(new NoOrdersAvailableException());
    }
    return movingToClientGateway.reportArrival(lastOrder);
  }
}
