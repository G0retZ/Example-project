package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException;
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class PreOrderProcessGateway implements OrderConfirmationGateway {

  @NonNull
  private final ApiService apiService;

  @Inject
  public PreOrderProcessGateway(@NonNull ApiService apiService) {
    this.apiService = apiService;
  }

  @NonNull
  @Override
  public Single<String> sendDecision(@NonNull Order order, boolean accepted) {
    return apiService.sendPreOrderProcess(new ApiOrderDecision(order.getId(), accepted))
        .subscribeOn(Schedulers.io())
        .map(apiSimpleResult -> {
          String message = apiSimpleResult.getMessage();
          if ("200".equals(apiSimpleResult.getCode())) {
            return message == null ? "" : message;
          } else {
            throw new OrderConfirmationFailedException(message == null ? "" : message);
          }
        });
  }
}
