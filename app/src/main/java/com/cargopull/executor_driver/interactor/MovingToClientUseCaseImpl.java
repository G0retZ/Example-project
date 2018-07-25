package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;
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
    return movingToClientGateway.reportArrival();
  }
}
