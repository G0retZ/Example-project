package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class UpcomingPreOrderMessagesUseCaseImpl implements UpcomingPreOrderMessagesUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final CommonGateway<String> gateway;
  @Nullable
  private Flowable<String> messagesFlowable;

  @Inject
  public UpcomingPreOrderMessagesUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull CommonGateway<String> gateway) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<String> getUpcomingPreOrderMessages() {
    if (messagesFlowable == null) {
      messagesFlowable = gateway.getData()
          .observeOn(Schedulers.single())
          .doOnError(errorReporter::reportError)
          .replay(1)
          .refCount();
    }
    return messagesFlowable;
  }
}
