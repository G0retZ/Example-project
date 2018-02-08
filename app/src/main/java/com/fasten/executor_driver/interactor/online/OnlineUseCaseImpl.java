package com.fasten.executor_driver.interactor.online;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

public class OnlineUseCaseImpl implements OnlineUseCase {

  @NonNull
  private final OnlineGateway gateway;

  OnlineUseCaseImpl(@NonNull OnlineGateway gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Completable goOnline() {
    return gateway.goOnline();
  }
}
