package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class ExecutorBalanceUseCaseImpl implements ExecutorBalanceUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ExecutorBalanceGateway gateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private Flowable<ExecutorBalance> cancelOrderReasonsFlowable = Flowable.empty();

  @Inject
  public ExecutorBalanceUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull ExecutorBalanceGateway gateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<ExecutorBalance> getExecutorBalance(boolean reset) {
    if (reset) {
      cancelOrderReasonsFlowable = loginReceiver.get()
          .toFlowable(BackpressureStrategy.BUFFER)
          .switchMap(gateway::loadExecutorBalance)
          .doOnError(errorReporter::reportError)
          .replay(1)
          .refCount()
          // TODO: тут костыль о непонятном баге. На девайсах после ошибки новые подписчики не получают вообще ничего. Поэтому приходится подобным образо кешировать ошибку.
          .doOnError(throwable -> cancelOrderReasonsFlowable = Flowable.error(throwable));
    }
    return cancelOrderReasonsFlowable;
  }
}
