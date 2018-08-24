package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class MissedOrderUseCaseImpl implements MissedOrderUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final MissedOrderGateway gateway;
  @Nullable
  private Flowable<String> messagesFlowable;

  @Inject
  public MissedOrderUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull MissedOrderGateway gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<String> getMissedOrders() {
    if (messagesFlowable == null) {
      messagesFlowable = gateway.loadMissedOrdersMessages()
          .observeOn(Schedulers.single())
          .doOnError(errorReporter::reportError)
          .replay(1)
          .refCount();
    }
    return messagesFlowable;
  }
}
