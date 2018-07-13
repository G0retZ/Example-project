package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;
import javax.inject.Inject;

public class WaitingForClientUseCaseImpl implements WaitingForClientUseCase {

  @NonNull
  private final WaitingForClientGateway waitingForClientGateway;

  @Inject
  public WaitingForClientUseCaseImpl(@NonNull WaitingForClientGateway waitingForClientGateway) {
    this.waitingForClientGateway = waitingForClientGateway;
  }

  @NonNull
  @Override
  public Completable startTheOrder() {
    return waitingForClientGateway.startTheOrder();
  }
}
