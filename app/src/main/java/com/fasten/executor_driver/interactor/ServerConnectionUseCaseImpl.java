package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

public class ServerConnectionUseCaseImpl implements ServerConnectionUseCase {

  @NonNull
  private final ServerConnectionGateway serverConnectionGateway;

  public ServerConnectionUseCaseImpl(@NonNull ServerConnectionGateway serverConnectionGateway) {
    this.serverConnectionGateway = serverConnectionGateway;
  }

  @Override
  public Flowable<Boolean> connect() {
    return serverConnectionGateway.openSocket();
  }
}
