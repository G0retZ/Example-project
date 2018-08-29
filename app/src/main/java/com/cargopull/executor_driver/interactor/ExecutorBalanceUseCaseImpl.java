package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.ExecutorBalance;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ExecutorBalanceUseCaseImpl implements ExecutorBalanceUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CommonGateway<ExecutorBalance> gateway;
  @Nullable
  private Flowable<ExecutorBalance> cancelOrderReasonsFlowable;

  @Inject
  public ExecutorBalanceUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CommonGateway<ExecutorBalance> gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<ExecutorBalance> getExecutorBalance() {
    if (cancelOrderReasonsFlowable == null) {
      cancelOrderReasonsFlowable = gateway.getData()
          .observeOn(Schedulers.single())
          .doOnError(errorReporter::reportError)
          .replay(1)
          .refCount();
    }
    return cancelOrderReasonsFlowable;
  }
}
