package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.ForbiddenExecutorStateException;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public class ExecutorStateNotOnlineUseCaseImpl implements
    ExecutorStateNotOnlineUseCase {

  @NonNull
  private final ExecutorStateSwitchGateway executorStateSwitchGateway;
  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @Nullable
  private ExecutorState currentExecutorState;

  public ExecutorStateNotOnlineUseCaseImpl(
      @NonNull ExecutorStateSwitchGateway executorStateSwitchGateway,
      @NonNull ExecutorStateUseCase executorStateUseCase) {
    this.executorStateSwitchGateway = executorStateSwitchGateway;
    this.executorStateUseCase = executorStateUseCase;
  }

  @NonNull
  @Override
  public Flowable<ExecutorState> getExecutorStates() {
    return executorStateUseCase.getExecutorStates(false)
        .doOnNext(executorState -> currentExecutorState = executorState)
        .doOnTerminate(() -> currentExecutorState = null);
  }

  @NonNull
  @Override
  public Completable setExecutorNotOnline() {
    return currentExecutorState == null || currentExecutorState != ExecutorState.ONLINE
        ? Completable.error(new ForbiddenExecutorStateException())
        : executorStateSwitchGateway.sendNewExecutorState(ExecutorState.SHIFT_OPENED);
  }
}
