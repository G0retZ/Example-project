package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.interactor.MovingToClientGateway;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;

public class MovingToClientGatewayImpl implements MovingToClientGateway {

  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<String, Order> mapper;

  @Inject
  public MovingToClientGatewayImpl(
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
        .filter(executorState -> executorState == ExecutorState.MOVING_TO_CLIENT)
        .map(executorState -> {
          if (executorState.getData() == null) {
            throw new NoOrdersAvailableException();
          }
          return mapper.map(executorState.getData());
        }).observeOn(Schedulers.single());
  }

  @NonNull
  @Override
  public Completable callToClient() {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"CALL_TO_CLIENT\"")
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }

  @NonNull
  @Override
  public Completable reportArrival() {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.send(BuildConfig.TRIP_DESTINATION, "\"DRIVER_ARRIVED\"")
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.single());
    }
    return Completable.error(new ConnectionClosedException());
  }
}
