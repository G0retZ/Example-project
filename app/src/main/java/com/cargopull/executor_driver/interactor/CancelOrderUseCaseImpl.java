package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CancelOrderUseCaseImpl implements CancelOrderUseCase {

  @NonNull
  private final CancelOrderReasonsUseCase cancelOrderReasonsUseCase;
  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CancelOrderGateway gateway;

  @Inject
  public CancelOrderUseCaseImpl(
      @NonNull CancelOrderReasonsUseCase cancelOrderReasonsUseCase,
      @NonNull ErrorReporter errorReporter,
      @NonNull CancelOrderGateway gateway) {
    this.cancelOrderReasonsUseCase = cancelOrderReasonsUseCase;
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Completable cancelOrder(@NonNull CancelOrderReason cancelOrderReason) {
    return cancelOrderReasonsUseCase
        .getCancelOrderReasons()
        .firstOrError()
        .map(cancelOrderReasons -> {
          if (!cancelOrderReasons.contains(cancelOrderReason)) {
            throw new IndexOutOfBoundsException(
                "Невереная причина отказа: " + cancelOrderReason + "."
                    + "Доступные причины отказа: " + cancelOrderReasons);
          }
          return cancelOrderReason;
        }).subscribeOn(Schedulers.single())
        .doOnError(errorReporter::reportError)
        .flatMapCompletable(gateway::cancelOrder)
        .observeOn(Schedulers.single());
  }
}
