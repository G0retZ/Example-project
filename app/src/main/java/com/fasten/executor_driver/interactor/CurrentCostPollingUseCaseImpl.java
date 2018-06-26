package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;

public class CurrentCostPollingUseCaseImpl implements CurrentCostPollingUseCase {

  @NonNull
  private final CurrentCostPollingGateway currentCostPollingGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  public CurrentCostPollingUseCaseImpl(
      @NonNull CurrentCostPollingGateway currentCostPollingGateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.currentCostPollingGateway = currentCostPollingGateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Completable listenForPolling() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(channelId -> currentCostPollingGateway.startPolling(channelId).toFlowable())
        .flatMapCompletable(a -> Completable.complete());
  }
}
