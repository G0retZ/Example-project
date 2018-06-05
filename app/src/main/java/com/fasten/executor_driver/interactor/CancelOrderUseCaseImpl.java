package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.CancelOrderReason;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;

class CancelOrderUseCaseImpl implements CancelOrderUseCase {

  @NonNull
  private final CancelOrderGateway gateway;
  @NonNull
  private final SocketGateway socketGateway;
  @NonNull
  private Flowable<List<CancelOrderReason>> cancelOrderReasonsFlowable = Flowable.empty();
  @Nullable
  private List<CancelOrderReason> cancelOrderReasons;

  CancelOrderUseCaseImpl(@NonNull CancelOrderGateway gateway,
      @NonNull SocketGateway socketGateway) {
    this.gateway = gateway;
    this.socketGateway = socketGateway;
  }

  @NonNull
  @Override
  public Flowable<List<CancelOrderReason>> getCancelOrderReasons(boolean reset) {
    if (reset) {
      cancelOrderReasonsFlowable = gateway.loadCancelOrderReasons()
          .startWith(socketGateway.openSocket().toFlowable())
          .map(cancelOrderReasons1 -> cancelOrderReasons = cancelOrderReasons1)
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
    if (cancelOrderReasons == null || !cancelOrderReasons.contains(cancelOrderReason)) {
      return Completable.error(new IndexOutOfBoundsException());
    }
    return gateway.cancelOrder(cancelOrderReason);
  }
}
