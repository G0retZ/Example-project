package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class MissedOrderUseCaseImpl implements MissedOrderUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final MissedOrderGateway gateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  @Inject
  public MissedOrderUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull MissedOrderGateway gateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<String> getMissedOrders() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(gateway::loadMissedOrdersMessages)
        .doOnError(errorReporter::reportError);
  }
}
