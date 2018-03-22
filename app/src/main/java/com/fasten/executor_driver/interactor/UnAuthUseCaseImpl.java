package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;
import javax.inject.Inject;

public class UnAuthUseCaseImpl implements UnAuthUseCase {

  @NonNull
  private final UnAuthGateway gateway;

  @Inject
  public UnAuthUseCaseImpl(@NonNull UnAuthGateway gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Completable getUnauthorized() {
    return gateway.waitForUnauthorized();
  }
}
