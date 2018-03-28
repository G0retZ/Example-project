package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class ExecutorStateUseCaseImpl implements ExecutorStateUseCase {

  @NonNull
  private final ExecutorStateGateway gateway;
  @NonNull
  private final SocketGateway socketGateway;
  @NonNull
  private final DataReceiver<String> loginReceiver;

  public ExecutorStateUseCaseImpl(@NonNull ExecutorStateGateway gateway,
      @NonNull SocketGateway socketGateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.gateway = gateway;
    this.socketGateway = socketGateway;
    this.loginReceiver = loginReceiver;
  }

  @Override
  public Flowable<ExecutorState> getExecutorStates() {
    return loginReceiver.get()
        .toFlowable(BackpressureStrategy.BUFFER)
        .startWith(socketGateway.openSocket().toFlowable())
        .switchMap(gateway::getState);
  }
}
