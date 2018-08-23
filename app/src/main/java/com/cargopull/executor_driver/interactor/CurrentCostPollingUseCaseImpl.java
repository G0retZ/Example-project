package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

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
        .switchMap(channelId -> currentCostPollingGateway.startPolling().toObservable())
        .observeOn(Schedulers.single())
        .flatMapCompletable(a -> Completable.complete());
  }
}
