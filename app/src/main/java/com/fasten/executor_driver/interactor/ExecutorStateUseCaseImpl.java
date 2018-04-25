package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
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
  @NonNull
  private Flowable<ExecutorState> executorStateFlowable = Flowable.empty();

  @Inject
  public ExecutorStateUseCaseImpl(@NonNull ExecutorStateGateway gateway,
      @NonNull SocketGateway socketGateway,
      @NonNull DataReceiver<String> loginReceiver) {
    this.gateway = gateway;
    this.socketGateway = socketGateway;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<ExecutorState> getExecutorStates(boolean reset) {
    if (reset) {
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
