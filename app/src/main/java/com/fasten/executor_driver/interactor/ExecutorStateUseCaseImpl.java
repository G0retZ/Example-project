package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class ExecutorStateUseCaseImpl implements ExecutorStateUseCase {

  @NonNull
  private final ExecutorStateGateway gateway;
  @NonNull
  private final SocketGateway socketGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  @Nullable
  private Flowable<ExecutorState> executorStateFlowable;

  @Inject
  public ExecutorStateUseCaseImpl(@NonNull ExecutorStateGateway gateway,
      @NonNull SocketGateway socketGateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.gateway = gateway;
    this.socketGateway = socketGateway;
    this.loginReceiver = loginReceiver;
  }

  @Override
  public Flowable<ExecutorState> getExecutorStates(boolean reset) {
    if (executorStateFlowable == null || reset) {
      executorStateFlowable = loginReceiver.get()
          .toFlowable(BackpressureStrategy.BUFFER)
          .startWith(socketGateway.openSocket().toFlowable())
          .switchMap(gateway::getState)
          .replay(1)
          .refCount();
    }
    return executorStateFlowable;
  }
}
