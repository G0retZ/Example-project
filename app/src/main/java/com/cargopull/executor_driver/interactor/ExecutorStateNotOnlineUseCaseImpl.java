package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExecutorStateNotOnlineUseCaseImpl implements
    ExecutorStateNotOnlineUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ExecutorStateSwitchGateway gateway;
  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final List<ExecutorState> allowedExecutorStates;

  public ExecutorStateNotOnlineUseCaseImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull ExecutorStateSwitchGateway gateway,
      @NonNull ExecutorStateUseCase executorStateUseCase,
      @NonNull ExecutorState... allowed) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
    this.executorStateUseCase = executorStateUseCase;
    allowedExecutorStates = Collections.unmodifiableList(Arrays.asList(allowed));
  }

  @NonNull
  @Override
  public Completable setExecutorNotOnline() {
    return executorStateUseCase.getExecutorStates()
        .firstOrError()
        .map(executorState -> {
          if (!allowedExecutorStates.contains(executorState)) {
            throw new IllegalArgumentException(
                "Недопустимый статус перевозчика: " + executorState);
          }
          return ExecutorState.SHIFT_OPENED;
        }).doOnError(errorReporter::reportError)
        .flatMapCompletable(gateway::sendNewExecutorState)
        .observeOn(Schedulers.single());
  }
}
