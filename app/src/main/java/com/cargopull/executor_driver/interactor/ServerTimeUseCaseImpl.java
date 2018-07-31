package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.ErrorReporter;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import javax.inject.Inject;

class ServerTimeUseCaseImpl implements ServerTimeUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ServerTimeGateway gateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private final TimeUtils timeUtils;

  @Inject
  ServerTimeUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull ServerTimeGateway gateway,
      @NonNull DataReceiver<String> loginReceiver,
      @NonNull TimeUtils timeUtils) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
    this.loginReceiver = loginReceiver;
    this.timeUtils = timeUtils;
  }

  @NonNull
  @Override
  public Completable getServerTime() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap(gateway::loadServerTime)
        .doOnError(errorReporter::reportError)
        .flatMapCompletable(timeStamp -> Completable.fromAction(
            () -> timeUtils.setServerCurrentTime(timeStamp)
        ));
  }
}
