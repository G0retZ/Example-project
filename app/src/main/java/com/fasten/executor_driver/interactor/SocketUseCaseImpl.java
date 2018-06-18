package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

class SocketUseCaseImpl implements SocketUseCase {

  @NonNull
  private final SocketGateway socketGateway;

  SocketUseCaseImpl(@NonNull SocketGateway socketGateway) {
    this.socketGateway = socketGateway;
  }

  @Override
  public Flowable<Boolean> connect() {
    return socketGateway.openSocket();
  }
}
