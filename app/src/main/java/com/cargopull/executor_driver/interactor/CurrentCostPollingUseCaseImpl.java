package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CurrentCostPollingUseCaseImpl implements CurrentCostPollingUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CurrentCostPollingGateway gateway;
  @Nullable
  private Completable completable;

  @Inject
  public CurrentCostPollingUseCaseImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull CurrentCostPollingGateway gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Completable listenForPolling() {
    if (completable == null) {
      completable = gateway.startPolling()
          .observeOn(Schedulers.single())
          .doOnError(errorReporter::reportError);
    }
    return completable;
  }
}
