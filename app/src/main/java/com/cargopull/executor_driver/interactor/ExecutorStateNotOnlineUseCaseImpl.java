package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class ExecutorStateNotOnlineUseCaseImpl implements
    ExecutorStateNotOnlineUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ExecutorStateSwitchGateway executorStateSwitchGateway;
  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @Nullable
  private ExecutorState currentExecutorState;

  public ExecutorStateNotOnlineUseCaseImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull ExecutorStateSwitchGateway executorStateSwitchGateway,
      @NonNull ExecutorStateUseCase executorStateUseCase) {
    this.errorReporter = errorReporter;
    this.executorStateSwitchGateway = executorStateSwitchGateway;
    this.executorStateUseCase = executorStateUseCase;
  }

  @NonNull
  @Override
  public Flowable<ExecutorState> getExecutorStates() {
    return executorStateUseCase.getExecutorStates(false)
        .doOnNext(executorState -> currentExecutorState = executorState)
        .doOnTerminate(() -> currentExecutorState = null)
        .doOnError(errorReporter::reportError);
  }

  @NonNull
  @Override
  public Completable setExecutorNotOnline() {
    return Single.fromCallable(() -> {
      if (currentExecutorState == null || currentExecutorState != ExecutorState.ONLINE) {
        throw new IllegalArgumentException(
            "Недопустимый статус перевозчика: " + currentExecutorState);
      }
      return ExecutorState.SHIFT_OPENED;
    }).doOnError(errorReporter::reportError)
        .flatMapCompletable(executorStateSwitchGateway::sendNewExecutorState);
  }
}
