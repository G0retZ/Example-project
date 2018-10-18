package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CurrentCostPollingUseCaseImpl implements CurrentCostPollingUseCase {

  @NonNull
  private final CurrentCostPollingGateway gateway;
  @Nullable
  private Completable completable;

  @Inject
  public CurrentCostPollingUseCaseImpl(@NonNull CurrentCostPollingGateway gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Completable listenForPolling() {
    if (completable == null) {
      completable = gateway.startPolling().observeOn(Schedulers.single());
    }
    return completable;
  }
}
