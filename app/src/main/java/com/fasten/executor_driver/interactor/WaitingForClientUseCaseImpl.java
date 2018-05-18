package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class WaitingForClientUseCaseImpl implements WaitingForClientUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @NonNull
  private final WaitingForClientGateway waitingForClientGateway;

  @Inject
  public WaitingForClientUseCaseImpl(@NonNull OrderGateway orderGateway,
      @NonNull WaitingForClientGateway waitingForClientGateway) {
    this.orderGateway = orderGateway;
    this.waitingForClientGateway = waitingForClientGateway;
  }

  @Override
  public Flowable<Order> getOrders() {
    return orderGateway.getOrders(ExecutorState.WAITING_FOR_CLIENT);
  }

  @NonNull
  @Override
  public Completable callToClient() {
    return waitingForClientGateway.callToClient();
  }

  @NonNull
  @Override
  public Completable startTheOrder() {
    return waitingForClientGateway.startTheOrder();
  }
}
