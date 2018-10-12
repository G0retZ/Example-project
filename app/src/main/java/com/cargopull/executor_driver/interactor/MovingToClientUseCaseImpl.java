package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class MovingToClientUseCaseImpl implements MovingToClientUseCase {

  @NonNull
  private final MovingToClientGateway movingToClientGateway;

  @Inject
  public MovingToClientUseCaseImpl(@NonNull MovingToClientGateway movingToClientGateway) {
    this.movingToClientGateway = movingToClientGateway;
  }

  @NonNull
  @Override
  public Completable reportArrival() {
    return movingToClientGateway.reportArrival().observeOn(Schedulers.single());
  }
}
