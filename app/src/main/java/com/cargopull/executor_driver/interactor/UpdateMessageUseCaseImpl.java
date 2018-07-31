package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class UpdateMessageUseCaseImpl implements UpdateMessageUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final UpdateMessageGateway gateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  @Inject
  public UpdateMessageUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull UpdateMessageGateway gateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<String> getUpdateMessages() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(gateway::loadUpdateMessages)
        .doOnError(errorReporter::reportError);
  }
}
