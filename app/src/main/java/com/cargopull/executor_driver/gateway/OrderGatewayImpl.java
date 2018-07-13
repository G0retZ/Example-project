package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.OrderGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderGatewayImpl implements OrderGateway {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final ExecutorState executorState;
  @NonNull
  private final Mapper<String, Order> mapper;

  @Inject
  public OrderGatewayImpl(
      @NonNull ExecutorStateUseCase executorStateUseCase,
      @NonNull ExecutorState executorState,
      @NonNull Mapper<String, Order> mapper) {
    this.executorStateUseCase = executorStateUseCase;
    this.executorState = executorState;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    return executorStateUseCase
        .getExecutorStates(false)
        .subscribeOn(Schedulers.single())
        .observeOn(Schedulers.computation())
        .filter(executorState1 -> executorState1 == executorState)
        .map(executorState1 -> {
          if (executorState1.getData() == null) {
            throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
          }
          return mapper.map(executorState1.getData());
        }).observeOn(Schedulers.single());
  }
}
