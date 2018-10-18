package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.ExecutorState;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ExecutorStateUseCaseImpl implements ExecutorStateUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CommonGateway<ExecutorState> gateway;
  @Nullable
  private Flowable<ExecutorState> executorStateFlowable;

  @Inject
  public ExecutorStateUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CommonGateway<ExecutorState> gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<ExecutorState> getExecutorStates() {
    if (executorStateFlowable == null) {
      executorStateFlowable = gateway.getData()
          .observeOn(Schedulers.single())
          .doOnError(errorReporter::reportError)
          .replay(1)
          .refCount();
    }
    return executorStateFlowable;
  }
}
