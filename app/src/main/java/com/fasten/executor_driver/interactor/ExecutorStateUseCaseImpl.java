package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class ExecutorStateUseCaseImpl implements ExecutorStateUseCase {

  @NonNull
  private final ExecutorStateGateway gateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private Flowable<ExecutorState> executorStateFlowable = Flowable.empty();

  @Inject
  public ExecutorStateUseCaseImpl(@NonNull ExecutorStateGateway gateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.gateway = gateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<ExecutorState> getExecutorStates(boolean reset) {
    if (reset) {
      executorStateFlowable = loginReceiver.get()
          .toFlowable(BackpressureStrategy.BUFFER)
          .switchMap(gateway::getState)
          .replay(1)
          .refCount()
          // TODO: тут костыль о непонятном баге. На девайсах после ошибки новые подписчики не получают вообще ничего. Поэтому приходится подобным образо кешировать ошибку.
          .doOnError(throwable -> executorStateFlowable = Flowable.error(throwable));
    }
    return executorStateFlowable;
  }
}
