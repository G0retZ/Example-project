package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ExecutorStateUseCaseImpl implements ExecutorStateUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ExecutorStateGateway gateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private Flowable<ExecutorState> executorStateFlowable = Flowable.empty();

  @Inject
  public ExecutorStateUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull ExecutorStateGateway gateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<ExecutorState> getExecutorStates(boolean reset) {
    if (reset) {
      executorStateFlowable = loginReceiver.get()
          .toFlowable(BackpressureStrategy.BUFFER)
          .switchMap(login -> gateway.getState())
          .observeOn(Schedulers.single())
          .doOnError(errorReporter::reportError)
          .replay(1)
          .refCount()
          // TODO: тут костыль о непонятном баге. На девайсах после ошибки новые подписчики не получают вообще ничего. Поэтому приходится подобным образо кешировать ошибку.
          .doOnError(throwable -> executorStateFlowable = Flowable.error(throwable));
    }
    return executorStateFlowable;
  }
}
