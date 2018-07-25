package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.OrderCostDetailsGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderCostDetailsGatewayImpl implements OrderCostDetailsGateway {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final Mapper<String, OrderCostDetails> mapper;

  @Inject
  public OrderCostDetailsGatewayImpl(
      @NonNull ExecutorStateUseCase executorStateUseCase,
      @NonNull Mapper<String, OrderCostDetails> mapper) {
    this.executorStateUseCase = executorStateUseCase;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<OrderCostDetails> getOrderCostDetails() {
    return executorStateUseCase
        .getExecutorStates(false)
        .subscribeOn(Schedulers.single())
        .observeOn(Schedulers.computation())
        .filter(executorState1 -> executorState1 == ExecutorState.PAYMENT_CONFIRMATION)
        .map(executorState1 -> {
          if (executorState1.getData() == null) {
            throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
          }
          return mapper.map(executorState1.getData());
        }).observeOn(Schedulers.single());
  }
}
