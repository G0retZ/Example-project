package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PreOrderExpiredException;
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class PreOrderConfirmationGatewayImpl implements OrderConfirmationGateway {

  @NonNull
  private final ApiService apiService;

  @Inject
  public PreOrderConfirmationGatewayImpl(@NonNull ApiService apiService) {
    this.apiService = apiService;
  }

  @NonNull
  @Override
  public Single<String> sendDecision(@NonNull Order order, boolean accepted) {
    return apiService.sendPreOrderDecision(new ApiOrderDecision(order.getId(), accepted))
        .subscribeOn(Schedulers.io())
        .map(apiSimpleResult -> {
          if ("200".equals(apiSimpleResult.getCode())) {
            return apiSimpleResult.getMessage();
          } else {
            throw new PreOrderExpiredException(apiSimpleResult.getMessage());
          }
        })
        .observeOn(Schedulers.single());
  }
}
