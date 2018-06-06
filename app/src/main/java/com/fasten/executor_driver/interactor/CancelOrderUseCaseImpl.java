package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.CancelOrderReason;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;
import javax.inject.Inject;

public class CancelOrderUseCaseImpl implements CancelOrderUseCase {

  @NonNull
  private final CancelOrderGateway gateway;
  @NonNull
  private final SocketGateway socketGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private Flowable<List<CancelOrderReason>> cancelOrderReasonsFlowable = Flowable.empty();
  @Nullable
  private List<CancelOrderReason> cancelOrderReasons;

  @Inject
  public CancelOrderUseCaseImpl(@NonNull CancelOrderGateway gateway,
      @NonNull SocketGateway socketGateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.gateway = gateway;
    this.socketGateway = socketGateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<List<CancelOrderReason>> getCancelOrderReasons(boolean reset) {
    if (reset) {
      cancelOrderReasonsFlowable = loginReceiver.get()
          .toFlowable(BackpressureStrategy.BUFFER)
          .startWith(socketGateway.openSocket().toFlowable())
          .switchMap(gateway::loadCancelOrderReasons)
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
