package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
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
    return waitingForClientGateway.startTheOrder().observeOn(Schedulers.single());
  }
}
