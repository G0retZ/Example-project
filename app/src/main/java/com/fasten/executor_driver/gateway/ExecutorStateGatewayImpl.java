package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.interactor.ExecutorStateGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ExecutorStateGatewayImpl implements ExecutorStateGateway {

  @NonNull
  private final ApiService api;

  @Inject
  public ExecutorStateGatewayImpl(@NonNull ApiService api) {
    this.api = api;
  }

  @NonNull
  @Override
  public Single<ExecutorState> getState() {
    return api.getMyStatus()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single())
        .map(string -> {
          try {
            return ExecutorState.valueOf(string);
          } catch (Exception e) {
            throw new DataMappingException("Ошибка маппинга: неверный формат статуса!", e);
          }
        });
  }
}
