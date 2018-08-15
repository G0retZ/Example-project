package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class PreOrderUseCaseImpl implements OrderUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final PreOrderGateway preOrderGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  @Inject
  public PreOrderUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull PreOrderGateway preOrderGateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.errorReporter = errorReporter;
    this.preOrderGateway = preOrderGateway;
    this.loginReceiver = loginReceiver;
  }

  @Override
  public Flowable<Order> getOrders() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(preOrderGateway::getPreOrders)
        .doOnError(errorReporter::reportError);
  }
}
