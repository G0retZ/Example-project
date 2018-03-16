package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;
import io.reactivex.Observer;
import javax.inject.Inject;

public class UnAuthUseCaseImpl implements UnAuthUseCase {

  @NonNull
  private final UnAuthGateway gateway;
  @NonNull
  private final Observer<ExecutorState> executorStateObserver;

  @Inject
  public UnAuthUseCaseImpl(@NonNull UnAuthGateway gateway,
      @NonNull Observer<ExecutorState> executorStateObserver) {
    this.gateway = gateway;
    this.executorStateObserver = executorStateObserver;
  }

  @NonNull
  @Override
  public Completable getUnauthorized() {
    return gateway.waitForUnauthorized()
        .doOnComplete(() -> executorStateObserver.onNext(ExecutorState.UNAUTHORIZED));
  }
}
