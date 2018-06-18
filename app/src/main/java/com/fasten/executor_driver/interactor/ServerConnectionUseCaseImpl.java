package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

class ServerConnectionUseCaseImpl implements ServerConnectionUseCase {

  @NonNull
  private final ServerConnectionGateway serverConnectionGateway;

  ServerConnectionUseCaseImpl(@NonNull ServerConnectionGateway serverConnectionGateway) {
    this.serverConnectionGateway = serverConnectionGateway;
  }

  @Override
  public Flowable<Boolean> connect() {
    return serverConnectionGateway.openSocket();
  }
}
