package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderConfirmationGatewayImpl implements OrderConfirmationGateway {

  @NonNull
  private final ApiService apiService;
  @NonNull
  private final Mapper<Throwable, Throwable> errorMapper;

  @Inject
  public OrderConfirmationGatewayImpl(@NonNull ApiService apiService,
      @NonNull Mapper<Throwable, Throwable> errorMapper) {
    this.apiService = apiService;
    this.errorMapper = errorMapper;
  }

  @NonNull
  @Override
  public Single<String> sendDecision(@NonNull Order order, boolean accepted) {
    return apiService.acceptOrderOffer(new ApiOrderDecision(order.getId(), accepted))
        .subscribeOn(Schedulers.io())
        .onErrorResumeNext(e -> Completable.error(errorMapper.map(e)))
        .toSingleDefault("");
  }
}
