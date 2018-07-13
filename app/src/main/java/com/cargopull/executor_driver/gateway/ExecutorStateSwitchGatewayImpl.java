package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateSwitchGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.client.StompClient;

public class ExecutorStateSwitchGatewayImpl implements ExecutorStateSwitchGateway {

  @NonNull
  private final StompClient stompClient;

  public ExecutorStateSwitchGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Completable sendNewExecutorState(ExecutorState executorState) {
    return stompClient
        .send(BuildConfig.SET_STATUS_DESTINATION, "\"" + executorState.toString() + "\"")
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
