package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.ErrorReporter;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ServerTimeUseCaseImpl implements ServerTimeUseCase {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final ServerTimeGateway gateway;
  @NonNull
  private final TimeUtils timeUtils;

  @Inject
  public ServerTimeUseCaseImpl(@NonNull ErrorReporter errorReporter,
      @NonNull ServerTimeGateway gateway,
      @NonNull TimeUtils timeUtils) {
    this.errorReporter = errorReporter;
    this.gateway = gateway;
    this.timeUtils = timeUtils;
  }

  @NonNull
  @Override
  public Completable getServerTime() {
    return gateway.loadServerTime()
        .observeOn(Schedulers.single())
        .doOnError(errorReporter::reportError)
        .flatMapCompletable(timeStamp -> Completable.fromAction(
            () -> timeUtils.setServerCurrentTime(timeStamp)
        ));
  }
}
