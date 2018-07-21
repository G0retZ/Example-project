package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.util.List;
import javax.inject.Inject;

public class CancelOrderUseCaseImpl implements CancelOrderUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CancelOrderGateway gateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private Flowable<List<CancelOrderReason>> cancelOrderReasonsFlowable = Flowable.empty();
  @Nullable
  private List<CancelOrderReason> cancelOrderReasons;

  @Inject
  public CancelOrderUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CancelOrderGateway gateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<List<CancelOrderReason>> getCancelOrderReasons(boolean reset) {
    if (reset) {
      cancelOrderReasonsFlowable = loginReceiver.get()
          .toFlowable(BackpressureStrategy.BUFFER)
          .switchMap(gateway::loadCancelOrderReasons)
          .map(cancelOrderReasons1 -> cancelOrderReasons = cancelOrderReasons1)
          .doOnError(errorReporter::reportError)
          .replay(1)
          .refCount()
          // TODO: тут костыль о непонятном баге. На девайсах после ошибки новые подписчики не получают вообще ничего. Поэтому приходится подобным образо кешировать ошибку.
          .doOnError(throwable -> cancelOrderReasonsFlowable = Flowable.error(throwable));
    }
    return cancelOrderReasonsFlowable;
  }

  @NonNull
  @Override
  public Completable cancelOrder(@NonNull CancelOrderReason cancelOrderReason) {
    return Single.fromCallable(() -> {
      if (cancelOrderReasons == null || !cancelOrderReasons.contains(cancelOrderReason)) {
        throw new IndexOutOfBoundsException("Невереная причина отказа: " + cancelOrderReason + "."
            + "Доступные причины отказа: " + cancelOrderReasons);
      }
      return cancelOrderReason;
    }).doOnError(errorReporter::reportError)
        .flatMapCompletable(gateway::cancelOrder);
  }
}
