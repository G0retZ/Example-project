package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ServerTimeUseCaseImpl implements ServerTimeUseCase {

  @NonNull
  private final CommonGateway<Long> gateway;
  @NonNull
  private final TimeUtils timeUtils;

  @Inject
  public ServerTimeUseCaseImpl(@NonNull CommonGateway<Long> gateway,
      @NonNull TimeUtils timeUtils) {
    this.gateway = gateway;
    this.timeUtils = timeUtils;
  }

  @NonNull
  @Override
  public Completable getServerTime() {
    return gateway.getData()
        .observeOn(Schedulers.single())
        .flatMapCompletable(timeStamp -> Completable.fromAction(
            () -> timeUtils.setServerCurrentTime(timeStamp)
        ));
  }
}
