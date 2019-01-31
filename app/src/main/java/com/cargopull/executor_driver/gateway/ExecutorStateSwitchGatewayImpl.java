package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateSwitchGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class ExecutorStateSwitchGatewayImpl implements ExecutorStateSwitchGateway {

  @NonNull
  private final ApiService apiService;

  public ExecutorStateSwitchGatewayImpl(@NonNull ApiService apiService) {
    this.apiService = apiService;
  }

  @NonNull
  @Override
  public Completable sendNewExecutorState(ExecutorState executorState) {
    return apiService.switchStatus(executorState)
        .subscribeOn(Schedulers.io());
  }
}
