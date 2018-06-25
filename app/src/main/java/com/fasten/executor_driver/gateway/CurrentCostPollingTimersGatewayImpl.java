package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.interactor.CurrentCostPollingTimersGateway;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.utils.Pair;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class CurrentCostPollingTimersGatewayImpl implements CurrentCostPollingTimersGateway {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final ExecutorState executorState;
  @NonNull
  private final Mapper<String, Pair<Long, Long>> mapper;

  @Inject
  public CurrentCostPollingTimersGatewayImpl(
      @NonNull ExecutorStateUseCase executorStateUseCase,
      @NonNull ExecutorState executorState,
      @NonNull Mapper<String, Pair<Long, Long>> mapper) {
    this.executorStateUseCase = executorStateUseCase;
    this.executorState = executorState;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Pair<Long, Long>> getPollingTimers() {
    return executorStateUseCase
        .getExecutorStates(false)
        .filter(executorState1 -> executorState1 == executorState)
        .map(executorState1 -> {
          if (executorState1.getData() == null) {
            throw new NoOrdersAvailableException();
          }
          return mapper.map(executorState1.getData());
        });
  }
}
