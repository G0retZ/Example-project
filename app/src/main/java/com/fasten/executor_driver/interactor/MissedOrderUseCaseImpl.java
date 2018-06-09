package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class MissedOrderUseCaseImpl implements MissedOrderUseCase {

  @NonNull
  private final MissedOrderGateway gateway;
  @NonNull
  private final SocketGateway socketGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  @Inject
  public MissedOrderUseCaseImpl(@NonNull MissedOrderGateway gateway,
      @NonNull SocketGateway socketGateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.gateway = gateway;
    this.socketGateway = socketGateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<String> getMissedOrders() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .startWith(socketGateway.openSocket().toFlowable())
        .switchMap(gateway::loadMissedOrdersMessages);
  }
}
