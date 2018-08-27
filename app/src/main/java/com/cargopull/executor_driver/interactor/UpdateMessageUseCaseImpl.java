package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class UpdateMessageUseCaseImpl implements UpdateMessageUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final UpdateMessageGateway gateway;

  @Inject
  public UpdateMessageUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull UpdateMessageGateway gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<String> getUpdateMessages() {
    return gateway.loadUpdateMessages()
        .observeOn(Schedulers.single())
        .doOnError(errorReporter::reportError);
  }
}
