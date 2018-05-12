package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.interactor.OrderGateway;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class DriverOrderConfirmationGatewayImpl implements OrderGateway {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<String, Order> mapper;

  @Inject
  public DriverOrderConfirmationGatewayImpl(
      @NonNull ExecutorStateUseCase executorStateUseCase,
      @NonNull StompClient stompClient,
      @NonNull Mapper<String, Order> mapper) {
    this.executorStateUseCase = executorStateUseCase;
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Flowable<Order> getOrders() {
    return executorStateUseCase
        .getExecutorStates(false)
        .subscribeOn(Schedulers.single())
        .observeOn(Schedulers.computation())
        .filter(executorState -> executorState == ExecutorState.DRIVER_ORDER_CONFIRMATION)
        .map(executorState -> {
          if (executorState.getData() == null) {
            throw new NoOrdersAvailableException();
          }
          return mapper.map(executorState.getData());
        }).observeOn(Schedulers.single());
  }

  @NonNull
  @Override
  public Completable sendDecision(@NonNull Order order, boolean accepted) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(
          BuildConfig.CONFIRM_OFFER_DESTINATION,
          "{\"id\":\"" + order.getId() + "\", \"approved\":\"" + accepted + "\"}"
      )
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }
}
