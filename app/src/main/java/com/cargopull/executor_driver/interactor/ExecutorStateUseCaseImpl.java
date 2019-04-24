package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.utils.EmptyEmitter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ExecutorStateUseCaseImpl implements ExecutorStateUseCase,
    DataUpdateUseCase<ExecutorState> {

  @NonNull
  private final CommonGateway<ExecutorState> gateway;
  @Nullable
  private Flowable<ExecutorState> executorStateFlowable;
  @NonNull
  private Emitter<ExecutorState> emitter = new EmptyEmitter<>();

  @Inject
  public ExecutorStateUseCaseImpl(@NonNull CommonGateway<ExecutorState> gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<ExecutorState> getExecutorStates() {
    if (executorStateFlowable == null) {
      executorStateFlowable = Flowable.merge(
          Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER),
          gateway.getData()
              .observeOn(Schedulers.single())
              .doOnComplete(() -> emitter.onComplete())
      ).replay(1).refCount();
    }
    return executorStateFlowable;
  }

  @Override
  public void updateWith(@NonNull ExecutorState data) {
    emitter.onNext(data);
  }
}
