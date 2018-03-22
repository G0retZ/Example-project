package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;
import io.reactivex.Observer;

public class ExecutorStateUseCaseImpl implements ExecutorStateUseCase {

  @NonNull
  private final ExecutorStateGateway gateway;
  @NonNull
  private final Observer<ExecutorState> executorStateObserver;

  public ExecutorStateUseCaseImpl(@NonNull ExecutorStateGateway gateway,
      @NonNull Observer<ExecutorState> executorStateObserver) {
    this.gateway = gateway;
    this.executorStateObserver = executorStateObserver;
  }

  @Override
  public Completable loadStatus() {
    return gateway.getState().doOnSuccess(executorStateObserver::onNext).toCompletable();
  }
}
