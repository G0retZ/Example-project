package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;

public class ExecutorStateSwitchUseCaseImpl implements ExecutorStateSwitchUseCase {

  @NonNull
  private final ExecutorStateSwitchGateway executorStateSwitchGateway;

  ExecutorStateSwitchUseCaseImpl(
      @NonNull ExecutorStateSwitchGateway executorStateSwitchGateway) {
    this.executorStateSwitchGateway = executorStateSwitchGateway;
  }

  @NonNull
  @Override
  public Completable setExecutorState(ExecutorState executorState) {
    return executorStateSwitchGateway.sendNewExecutorState(executorState);
  }
}
