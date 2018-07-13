package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;
import javax.inject.Inject;

public class CallToClientUseCaseImpl implements CallToClientUseCase {

  @NonNull
  private final CallToClientGateway callToClientGateway;

  @Inject
  public CallToClientUseCaseImpl(@NonNull CallToClientGateway callToClientGateway) {
    this.callToClientGateway = callToClientGateway;
  }

  @NonNull
  @Override
  public Completable callToClient() {
    return callToClientGateway.callToClient();
  }
}
