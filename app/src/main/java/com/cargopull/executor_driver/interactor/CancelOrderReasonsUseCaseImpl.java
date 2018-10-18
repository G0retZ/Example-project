package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class CancelOrderReasonsUseCaseImpl implements CancelOrderReasonsUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CommonGateway<List<CancelOrderReason>> gateway;
  @Nullable
  private Flowable<List<CancelOrderReason>> cancelOrderReasonsFlowable;

  @Inject
  public CancelOrderReasonsUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CommonGateway<List<CancelOrderReason>> gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<List<CancelOrderReason>> getCancelOrderReasons() {
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
