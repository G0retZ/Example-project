package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.Completable;
import javax.inject.Inject;

public class UnAuthUseCaseImpl implements UnAuthUseCase {

  @NonNull
  private final UnAuthGateway gateway;
  @NonNull
  private final DataSharer<ExecutorState> executorStateSharer;

  @Inject
  public UnAuthUseCaseImpl(@NonNull UnAuthGateway gateway,
      @NonNull DataSharer<ExecutorState> executorStateSharer) {
    this.gateway = gateway;
    this.executorStateSharer = executorStateSharer;
  }

  @NonNull
  @Override
  public Completable getUnauthorized() {
    return gateway.waitForUnauthorized()
        .doOnComplete(() -> executorStateSharer.share(ExecutorState.UNAUTHORIZED));
  }
}
