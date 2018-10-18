package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class UpdateMessageUseCaseImpl implements UpdateMessageUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CommonGateway<String> gateway;

  @Inject
  public UpdateMessageUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CommonGateway<String> gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<String> getUpdateMessages() {
    return gateway.getData()
        .observeOn(Schedulers.single())
        .doOnError(errorReporter::reportError);
  }
}
